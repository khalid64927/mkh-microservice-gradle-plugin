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

import de.aaschmid.gradle.plugins.cpd.Cpd
import de.aaschmid.gradle.plugins.cpd.CpdExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.language.base.plugins.LifecycleBasePlugin

class CPDPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("de.aaschmid.cpd")
            configure<CpdExtension> {
                language = "java"
                toolVersion = "6.0.0"
                isIgnoreFailures = false
                minimumTokenCount = 50
            }

            // CPD Plugin already creates the task so we'll just reconfigure it.
            tasks.named("cpdCheck", Cpd::class.java) {
                description = "Runs cpd."
                group = "verification"

                reports.text.required.set(true)
                reports.xml.required.set(true)

                encoding = "UTF-8"
                source = fileTree("src").filter { source ->
                    source.name.endsWith(".java")
                }.asFileTree
            }
            tasks.named(LifecycleBasePlugin.CHECK_TASK_NAME).configure { dependsOn("cpdCheck") }
        }
    }
}
