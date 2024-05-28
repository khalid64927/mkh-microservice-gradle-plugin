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

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.owasp.dependencycheck.gradle.extension.DependencyCheckExtension
import org.owasp.dependencycheck.reporting.ReportGenerator.Format
import java.io.File

// TODO: add to extension
class OSSScanPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("org.owasp.dependencycheck")
            configure<DependencyCheckExtension> {
                format = Format.HTML.name
                outputDirectory = "$projectDir/build/oss/reports"
                failBuildOnCVSS = 7.0f
            }

            tasks.register("dependencyCache") {
                if (project.file("build/dependency-scan-results/cachetext").exists()) {
                    val outputDir = layout.buildDirectory.dir("dependency-scan-results").get().asFile.path
                    val cachetextFile = outputDir.plus("/cachetext")
                    val cachetextContent = File(cachetextFile)
                    println(cachetextContent.readText())
                }
            }
        }
    }
}
