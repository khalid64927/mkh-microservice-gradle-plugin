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
package com.github.khalid64927.gradle.publish

import io.github.gradlenexus.publishplugin.NexusPublishExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import java.net.URI

/**
 * Used to publish artifacts to Nexus Repository [For open Source Projects]
 * */
class OSSNexusPublicationPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        with(target.plugins) {
            apply("io.github.gradle-nexus.publish-plugin")
        }

        target.configure<NexusPublishExtension> {
            repositories {
                sonatype {
                    nexusUrl.set(URI.create("https://s01.oss.sonatype.org/service/local/"))
                    username.set(System.getenv("OSSRH_USER"))
                    password.set(System.getenv("OSSRH_KEY"))
                }
            }
        }
    }
}
