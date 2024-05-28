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

import com.github.khalid64927.gradle.utils.optionalStringProperty
import com.github.spotbugs.snom.Confidence
import com.github.spotbugs.snom.Effort
import com.github.spotbugs.snom.SpotBugsExtension
import com.github.spotbugs.snom.SpotBugsTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.withType

class SpotbugsPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("com.github.spotbugs")

            configure<SpotBugsExtension> {
                ignoreFailures.set(false)
                showStackTraces.set(true)
                showProgress.set(true)
                effort.set(Effort.DEFAULT)
                reportLevel.set(Confidence.DEFAULT)
                visitors.set(listOf("FindSqlInjection", "SwitchFallthrough"))
                omitVisitors.set(listOf("FindNonShortCircuit"))
                reportsDir.set(file("$projectDir/build/reports"))
                // check if this has baseline
                optionalStringProperty("\"$projectDir/quality/baseline-spotbugs.xml\"")?.run {
                    var file = file(this)
                    if (file.exists()) {
                        baselineFile.set(file)
                    }
                }
            }
            tasks.withType<SpotBugsTask> {
                val reportPath = "$projectDir/build/reports/spotbugs.html"
                reports.create("html") {
                    required.set(true)
                    outputLocation.set(file(reportPath))
                    setStylesheet("fancy-hist.xsl")
                }
                doLast {
                    val file = file(reportPath)
                    if (file.exists()) {
                        println(println("spotbugs report : ${file.absolutePath}"))
                    } else {
                        println(println("spotbugs report not generated"))
                    }
                }
            }
        }
    }
}
