/*
 * Copyright 2024 Mohammed Khalid Hamid.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.khalid64927.gradle

import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.attributes.Usage
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.Directory
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Classpath
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import org.gradle.process.ExecOperations
import org.gradle.workers.WorkAction
import org.gradle.workers.WorkParameters
import org.gradle.workers.WorkerExecutor
import java.io.File
import javax.inject.Inject

@CacheableTask abstract class KtLintTask : DefaultTask() {
    @Input lateinit var version: String

    @get:Classpath abstract val classpath: ConfigurableFileCollection

    @OutputDirectory lateinit var outputDirectory: Provider<Directory>

    init {
        group = "verification"
        description = "Runs ktlint."
    }

    @get:Inject internal abstract val workerExecutor: WorkerExecutor

    @TaskAction fun run() {
        val queue = workerExecutor.noIsolation()

        queue.submit(KtLintWorker::class.java) {
            classpath.from(classpath)
            outputDirectory.set(outputDirectory.get())
        }
    }
}

internal interface KtLintParameters : WorkParameters {
    val classpath: ConfigurableFileCollection
    val outputDirectory: DirectoryProperty
}

internal abstract class KtLintWorker @Inject internal constructor(
    private val execOperations: ExecOperations,
) : WorkAction<KtLintParameters> {
    override fun execute() {
        execOperations.javaexec {
            mainClass.set("com.pinterest.ktlint.Main")
            classpath = parameters.classpath
            args(
                "--reporter=plain",
                "--reporter=checkstyle," +
                    "output=${File(
                        parameters.outputDirectory.asFile.get(),
                        "ktlint-checkstyle-report.xml",
                    )}",
                "**/*.kt",
                "**/*.kts",
                "!build/",
                "!build/**",
            )
        }
    }
}

private fun Project.kotlinFiles(baseDir: String? = null) =
    fileTree(baseDir ?: projectDir)
        .setIncludes(listOf("**/*.kt", "**/*.kts"))
        .setExcludes(
            listOf(
                "build/",
                "generated/",
                "src/test/snapshots/", // Paparazzi.
            ),
        )

private fun Project.editorconfigFile() = fileTree(mapOf("dir" to ".", "include" to ".editorconfig"))
fun Project.addKTLintTask() {
    val ktlintConfiguration = configurations.create("ktlint") {
        attributes {
            attribute(Usage.USAGE_ATTRIBUTE, objects.named(Usage::class.javaObjectType, Usage.JAVA_RUNTIME))
        }

        isCanBeConsumed = false
        isVisible = false

        defaultDependencies {
            // TODO
            // add(dependencies.create("com.pinterest.ktlint:ktlint-cli:"))
        }
    }
    tasks.register("ktlint", KtLintTask::class.java) {
        version = "1.0.1"
        classpath.from(ktlintConfiguration)
        outputDirectory = layout.buildDirectory.dir("reports/ktlint/")
        inputs.files(kotlinFiles(), rootProject.editorconfigFile())
    }
}
