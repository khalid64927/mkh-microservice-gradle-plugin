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

import io.gitlab.arturbosch.detekt.Detekt
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.TaskCollection
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.withType

class DetektPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        with(target.plugins) {
            apply("io.gitlab.arturbosch.detekt")
        }

        val detektTasks: TaskCollection<Detekt> = target.tasks.withType()
        target.afterEvaluate {
            detektTasks.configureEach {
                this.source = source.matching {
                    exclude {
                        it.file.path.startsWith(target.buildDir.path)
                    }
                }
            }
        }
        val detektTask = target.tasks.register("detektWithoutTests") {
            group = "verification"

            dependsOn(detektTasks.matching { it.name.contains("Test").not() })
        }
        target.tasks.getByName("check").dependsOn(detektTask)

        target.dependencies {
            "detektPlugins"("io.gitlab.arturbosch.detekt:detekt-formatting:1.22.0")
        }
    }
}
