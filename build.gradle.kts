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
import com.diffplug.gradle.spotless.SpotlessExtension
import com.diffplug.gradle.spotless.SpotlessPlugin
import java.util.*

buildscript {
    dependencies {
        classpath(libs.spotlessGradlePlugin)
    }
}

plugins {
    `kotlin-dsl`
    `maven-publish`
    signing
    java
}

apply<SpotlessPlugin>()

configure<SpotlessExtension>{
    kotlin {
        target ("**/*.kt")
        targetExclude("**/build/**/*.kt", "**/spotless/**/*.kt")
        ktlint("0.48.0").editorConfigOverride(mapOf("disabled_rules" to "no-wildcard-imports"))
        licenseHeaderFile(project.rootProject.file("spotless/Copyright.kt"))
    }
    format("kts") {
        target("**/*.kts")
        targetExclude("**/build/**/*.kts", "**/spotless/**/*.kts")
        licenseHeaderFile(rootProject.file("spotless/Copyright.kts"), "(^(?![\\/ ]\\*).*$)")
    }
    format("xml") {
        target("**/*.xml")
        targetExclude("**/build/**/*.xml",  "**/spotless/**/*.xml")
        licenseHeaderFile(rootProject.file("spotless/Copyright.xml"), "(<[^!?])")
    }
}


group = "com.mkh.gradle"
version = "1.0.0-SNAPSHOT"


java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

repositories {
    mavenCentral()
    gradlePluginPortal()
    google()
    maven {
        url = uri("https://raw.githubusercontent.com/graalvm/native-build-tools/snapshots")
    }
    maven {
        url = uri("https://plugins.gradle.org/m2/")
    }
}

// Why are all these api ? because I want to ship it as FAT JAR
dependencies {
    api(libs.kotlinDslPlugin)
    implementation(libs.kotlinGradlePlugin)
    api(libs.detektGradlePlugin)
    // allows to publish to Nexus OSS repository
    api(libs.nexusPublishGradlePlugin)
    api(libs.jacocoGradlePlugin)
    // code formatter as per Ktlint
    api(libs.spotlessGradlePlugin)
    api(libs.sonarGradlePlugin)
    // checks for code duplication
    api(libs.cpdGradlePlugin)
    // Identifies dependencies that has updates
    api(libs.benManesGradlePlugin)
    // This is from OWASP [scans vulnerabilities in dependencies]
    api(libs.depCheckGradlePlugin)
    // This is from Sonatype [scans vulnerabilities in dependencies from Sonatype platforms: OSS Index and Nexus IQ Server]
    api(libs.scanGradlePlugin)
    // checks for code vulnerabilities
    api(libs.spotbugsGradlePlugin)
    // prints jacoco code coverage results in build logs
    api(libs.barfuinGradlePlugin)
    // Spring Boot Gradle Plugin
    api(libs.springBootGradlePlugin)
    // Graal VM native image build Gradle Plugin
    api(libs.graalVMGradlePlugin)
}


publishing {
    publications {
        register("mavenJava", MavenPublication::class) {
            from(components["java"])

            pom {
                name.set("khalid-gradle-plugin")
                description.set("This is a Gradle plugin with common build logic for all KMP libraries in my open source repo.")
                url.set("https://github.com/khalid64927/microservice-gradle-plugin")
                licenses {
                    license {
                        name.set("Apache-2.0")
                        distribution.set("repo")
                        url.set("https://github.com/khalid64927/microservice-gradle-plugin/blob/master/LICENSE.md")
                    }
                }

                developers {
                    developer {
                        id.set("khalid64927")
                        name.set("Mohammed Khalid Hamid")
                        email.set("khalid64927@gmail.com")
                    }
                }

                scm {
                    connection.set("scm:git:ssh://github.com/khalid64927/microservice-gradle-plugin.git")
                    developerConnection.set("scm:git:ssh://github.com/khalid64927/microservice-gradle-plugin.git")
                    url.set("https://github.com/khalid64927/khalid-gradle-plugin")
                }
            }
        }
    }

    repositories.maven("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/") {
        name = "OSSRH"

        credentials {
            username = System.getenv("OSSRH_USER")
            password = System.getenv("OSSRH_KEY")
        }
    }
}
signing {
    val signingPassword: String? = System.getenv("SIGNING_PASSWORD")
    val signingKey: String? = System.getenv("SIGNING_KEY")?.let { base64Key ->
        String(Base64.getDecoder().decode(base64Key))
    }
    if (signingKey != null && signingPassword != null) {
        useInMemoryPgpKeys(signingKey, signingPassword)
        sign(publishing.publications)
    }
}