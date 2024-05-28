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

import com.github.khalid64927.gradle.utils.isNative
import com.github.khalid64927.gradle.utils.requiredStringProperty
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.Exec
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.register
import org.springframework.boot.gradle.tasks.bundling.BootBuildImage

// TODO: add to extension
class SpringBootPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("org.springframework.boot")
            configure<BootBuildImage> {
                imageName.set(getCustomImageName())
                builder.set("paketobuildpacks/builder:tiny")
                docker {
                    publishRegistry {
                        username.set("${requiredStringProperty("registryUsername")}")
                        password.set("${requiredStringProperty("registryPassword")}")
                        url.set("${requiredStringProperty("registryUrl")}")
                    }
                }
                val mapOfImage = mapOf(
                    "BP_NATIVE_IMAGE" to isNative().toString(),
                    "BP_EMBED_CERTS" to "true",
                    "BP_JVM_VERSION" to "17", // TODO: set this from properties
                )
                environment.set(mapOfImage)
                bindings.set(getBuildBindings())

                // applicable only for GraalVM images
                if (isNative()) {
                    buildpacks.set(
                        listOf(
                            "urn:cnb:builder:paketo-buildpacks/ca-certificates",
                            "gcr.io/paketo-buildpacks/graalvm",
                            "gcr.io/paketo-buildpacks/java-native-image",
                        ),
                    )
                }
                if (project.properties["dynatraceBuildpackIncluded"] == "true") {
                    buildpacks.set(
                        listOf(
                            "urn:cnb:builder:paketo-buildpacks/java",
                            "docker://docker.io/paketobuildpacks/dynatrace",
                        ),
                    )
                }
            }

            tasks.register<Exec>("trivyScanImage") {
                dependsOn(tasks.named("bootBuildImage"))
                group = "verification"
                description = "Run Trivy scan on the Docker image"
                dependsOn(tasks.named("bootBuildImage"))

                doLast {
                    exec {
                        val imageName = "\"${project.name}:${project.version.toString().replace("+", "-")}"
                        // run Trivy scan
                        commandLine(
                            "trivy", "image", "--skip-update", "--format", "template",
                            "--template", "@/usr/local/share/trivy/templates/html.tpl",
                            "-o", "TrivyReport.html",
                            "${requiredStringProperty("registryUrl")}/${getImageBasePath(requiredStringProperty("imageBasePath"))}" +
                                "$imageName",
                        )
                    }
                    exec {
                        // convert html to pdf
                        commandLine("wkhtmltopdf", "TrivyReport.html", "TrivyReport.pdf")
                    }
                }
            }
        }
    }

    private fun Project.getCustomImageName() = "${requiredStringProperty("registryUrl")}" +
        "/" +
        "${getImageBasePath(requiredStringProperty("imageBasePath"))}" +
        "${project.name}:${project.version.toString().replace("+", "-")}"

    private fun getImageBasePath(imageBasePathProp: Any?) =
        imageBasePathProp?.toString()?.removeSuffix("/")?.removePrefix("/")?.plus("/") ?: ""
    private fun Project.getBuildBindings(): List<String> {
        var result = emptyList<String>()
        var buildpackVolumeBindings = requiredStringProperty("buildpackVolumeBindings")
        if (null == buildpackVolumeBindings || buildpackVolumeBindings !is String || buildpackVolumeBindings.isEmpty()) {
            return result
        }
        runCatching {
            result = buildpackVolumeBindings
                .split(",")
                .map {
                    it.trim()
                        .split(":", limit = 2)
                }
                .map {
                    val hostPath = "${it.last().trim()}"
                    val destPath = "/platform/bindings/${it.first().trim()}"
                    "$hostPath:$destPath"
                }.toList()
        }
        return result
    }
}
