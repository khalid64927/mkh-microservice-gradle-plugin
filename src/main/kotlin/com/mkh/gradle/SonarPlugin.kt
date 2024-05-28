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

import com.mkh.gradle.utils.optionalStringProperty
import com.mkh.gradle.utils.requiredStringProperty
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.withType
import org.sonarqube.gradle.SonarExtension
import org.sonarqube.gradle.SonarTask

class SonarPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("org.sonarqube")

            // validate properties
            val projectKey = requiredStringProperty("sonar.projectKey")
            val url = requiredStringProperty("sonar.url")
            var prKey = optionalStringProperty("bamboo_repository_pr_key")
            var prBranch = optionalStringProperty("bamboo_repository_pr_sourceBranch")
            var prBaseBranchName = optionalStringProperty("bamboo_repository_pr_targetBranch")
            var prBranchName = requiredStringProperty("bamboo_planRepository_1_branch")

            configure<SonarExtension> {
                properties {
                    property("sonar.host.url", url)
                    property("sonar.projectKey", projectKey)
                    if (!prKey.isNullOrBlank()) {
                        property("sonar.pullrequest.key", prKey)
                        property("sonar.pullrequest.branch", prBranch as String)
                        property("sonar.pullrequest.base", prBaseBranchName as String)
                    } else {
                        property("sonar.branch.name", prBranchName)
                    }

                    val jacocoAggregatedReport = "${rootDir.path}/build/reports/jacoco/jacocoAggregatedReport/jacocoAggregatedReport.xml"
                    if (file(jacocoAggregatedReport).exists()) {
                        property("sonar.coverage.jacoco.xmlReportPaths", jacocoAggregatedReport)
                    }

                    optionalStringProperty("sonar.exclusions")?.run {
                        property("sonar.exclusions", this)
                    }
                    optionalStringProperty("sonar.sources")?.run {
                        property("sonar.sources", this)
                    }
                    optionalStringProperty("sonar.tests")?.run {
                        property("sonar.tests", this)
                    }
                }
            }

            tasks.withType<SonarTask> {
                if (tasks.findByName("jacocoAggregatedReport") != null) {
                    dependsOn(tasks.getByName("jacocoAggregatedReport"))
                } else {
                    dependsOn(tasks.getByName("jacocoTestReport"))
                }
            }
        }
    }
}
