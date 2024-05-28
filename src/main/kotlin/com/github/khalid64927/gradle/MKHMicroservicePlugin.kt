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

import com.github.khalid64927.gradle.publish.OSSNexusPublicationPlugin
import com.github.khalid64927.gradle.publish.OSSPublicationPlugin
import com.github.khalid64927.gradle.utils.isNative
import com.github.khalid64927.gradle.utils.validateRequiredProperties
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.quality.Checkstyle

class MKHMicroservicePlugin : Plugin<Project> {

    override fun apply(target: Project) {
        validateRequiredProperties(target)
        with(target) {
            with(pluginManager) {
                apply(JvmPlugin::class.java)
                apply(StubJavaDocPlugin::class.java)
                apply(TestsReportPlugin::class.java)
                apply(SpringBootPlugin::class.java)
                apply(SpotlessPlugin::class.java)
                apply(DetektPlugin::class.java)
                apply(com.github.khalid64927.gradle.CPDPlugin::class.java)
                apply(Checkstyle::class.java)
                apply(SpotbugsPlugin::class.java)
                apply(OSSScanPlugin::class.java)
                apply(JacocoPlugin::class.java)
                apply(JacocoLogPlugin::class.java)
                apply(SonarPlugin::class.java)
                apply(OSSPublicationPlugin::class.java)
                apply(OSSNexusPublicationPlugin::class.java)
                if (isNative()) apply(GraalVMPlugin::class.java)
            }
            tasks.register("quality") {
                tasks.findByName("spotlessCheck")?.let { this.dependsOn(it) }
                tasks.findByName("checkstyleMain")?.let { this.dependsOn(it) }
                tasks.findByName("check")?.let { this.dependsOn(it) }
            }
        }
    }
}
