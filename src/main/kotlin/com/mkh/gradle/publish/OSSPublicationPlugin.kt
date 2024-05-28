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
package com.mkh.gradle.publish

import com.mkh.gradle.utils.requiredStringProperty
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.withType
import org.gradle.plugins.signing.SigningExtension
import java.util.Base64

/**
 * Used to publish artifacts to Maven Central Repository [For open Source Projects]
 * */

class OSSPublicationPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        with(target.plugins) {
            apply("org.gradle.maven-publish")
            apply("signing")
        }

        val libraryName: String = target.requiredStringProperty("publish.name")
        val description: String = target.requiredStringProperty("publish.description")
        val gitHubOrganization: String = target.requiredStringProperty("publish.repo.org")
        val gitHubName: String = target.requiredStringProperty("publish.repo.name")
        val license: String = target.requiredStringProperty("publish.license")
        val developersString: String = target.requiredStringProperty("publish.developers")
        val gitHubUrl = "https://github.com/$gitHubOrganization/$gitHubName"
        val sshUrl = "scm:git:ssh://github.com/$gitHubOrganization/$gitHubName.git"
        val developersList: List<Developer> = developersString.split(",").map { parseDeveloper(it) }

        target.configure<PublishingExtension> {
            publications.withType<MavenPublication> {
                // Provide artifacts information required by Maven Central
                pom {
                    this.name.set(libraryName)
                    this.description.set(description)
                    this.url.set(gitHubUrl)
                    licenses {
                        license {
                            this.name.set(license)
                            this.distribution.set("repo")
                            this.url.set("$gitHubUrl/blob/master/LICENSE.md")
                        }
                    }

                    developers {
                        developersList.forEach { dev ->
                            developer {
                                id.set(dev.id)
                                name.set(dev.name)
                                email.set(dev.email)
                            }
                        }
                    }

                    scm {
                        this.connection.set(sshUrl)
                        this.developerConnection.set(sshUrl)
                        this.url.set(gitHubUrl)
                    }
                }
            }
        }

        target.configure<SigningExtension> {
            val signingKeyId: String? = System.getenv("SIGNING_KEY_ID")
            val signingPassword: String? = System.getenv("SIGNING_PASSWORD")
            val signingKey: String? = System.getenv("SIGNING_KEY")?.let { base64Key ->
                String(Base64.getDecoder().decode(base64Key))
            }

            if (signingKeyId != null) {
                useInMemoryPgpKeys(signingKeyId, signingKey, signingPassword)
                sign(configuration.artifacts.toString())
            }
        }
    }
}
