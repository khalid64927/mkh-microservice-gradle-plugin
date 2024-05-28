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
package com.mkh.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.quality.Checkstyle
import org.gradle.api.plugins.quality.CheckstyleExtension
import org.gradle.kotlin.dsl.configure

// TODO: add to extension
class CheckStylePlugin : Plugin<Project> {

    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("checkstyle")
            configure<CheckstyleExtension> {
                toolVersion = "8.6"
                configFile = file("$projectDir/resources/mkh-checks.xml")
                isIgnoreFailures = false
                isShowViolations = true
            }

            tasks.register("checkstyle", Checkstyle::class.java) {
                description = "Runs checkstyle."
                group = "verification"
                source = fileTree("$projectDir/resources/mkh-checks.xml")
                include(listOf("**/*.java"))
                exclude(listOf("**/gen/**"))
                classpath = files()
                reports.html.required.set(true)
                reports.xml.required.set(true)
            }
        }
    }
}