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

import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.get
import org.gradle.kotlin.dsl.register
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

class JvmPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target.plugins) {
            apply("org.jetbrains.kotlin.jvm")
        }

        target.configure<JavaPluginExtension> {
            sourceCompatibility = JavaVersion.VERSION_17
            targetCompatibility = JavaVersion.VERSION_17
            withJavadocJar()
            withSourcesJar()
        }

        target.tasks
            .withType<KotlinCompile>()
            .configureEach {
                kotlinOptions.jvmTarget = "17"
            }

        target.plugins.withId("org.gradle.maven-publish") {
            target.configure<PublishingExtension> {
                publications.register("mavenJava", MavenPublication::class) {
                    from(target.components["java"])
                }
            }
        }
    }
}
