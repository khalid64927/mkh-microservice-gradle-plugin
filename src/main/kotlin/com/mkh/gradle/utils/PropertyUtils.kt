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
package com.mkh.gradle.utils

import org.gradle.api.GradleException
import org.gradle.api.JavaVersion
import org.gradle.api.Project

internal fun <T> Project.requiredProperty(name: String, mapper: (Any) -> T): T {
    val propertyName = "mkh.$name"
    val anyValue: Any = property(propertyName)
        ?: throw GradleException("Required property $propertyName not defined!")

    return try {
        mapper(anyValue)
    } catch (exc: Exception) {
        throw GradleException("Can't map property $propertyName to required type", exc)
    }
}

internal fun <T> Project.optionalProperty(name: String, mapper: (Any?) -> T?): T? {
    val propertyName = "mkh.$name"
    val anyValue: Any? = property(propertyName)
    return try {
        mapper(anyValue)
    } catch (exc: Exception) {
        println(" $propertyName is not found !")
        return null
    }
}

internal fun Project.optionalStringProperty(name: String): String? {
    return optionalProperty(name) { it?.toString() }
}

internal fun Any?.isNull() = (null == this)

internal fun Project.requiredIntProperty(name: String): Int {
    return requiredProperty(name) { it.toString().toInt() }
}

internal fun Project.requiredBooleanProperty(name: String): Boolean {
    return requiredProperty(name) { it.toString().toBoolean() }
}

internal fun Project.requiredStringProperty(name: String): String {
    return requiredProperty(name) { it.toString() }
}

internal fun Project.javaProperty(name: String): String {
    val supportedVersions = listOf("JAVA_17", "JAVA_18", "JAVA_19", "JAVA_20", "JAVA_21")
    val javaVersion = requiredStringProperty("javaVersion")
    val isValidEnum = JavaVersion.values()
        .map { it.toString() }
        .firstOrNull { it.equals(javaVersion, true) }
        ?.let { it in supportedVersions } ?: false

    if (isValidEnum) {
        throw GradleException("Provided  javaVersion $javaVersion is not a valid enum from JavaVersion")
    }

    return requiredProperty(name) { it.toString() }
}

fun validateRequiredProperties(target: Project) {
    target.run {
        var nexusUsername = requiredStringProperty("nexus.username")
        var nexusPassword = requiredStringProperty("nexus.password")
        var sonarProjectKey = requiredStringProperty("sonar.projectKey")
        var sonarProjectName = requiredStringProperty("sonar.projectName")
    }
}

private fun Project.isJavaProject(): Boolean {
    val isJava = plugins.hasPlugin("java")
    val isJavaLibrary = plugins.hasPlugin("java-library")
    val isJavaGradlePlugin = plugins.hasPlugin("java-gradle-plugin")
    return isJava || isJavaLibrary || isJavaGradlePlugin
}

private fun Project.isAndroidProject(): Boolean {
    val isAndroidLibrary = plugins.hasPlugin("com.android.library")
    val isAndroidApp = plugins.hasPlugin("com.android.application")
    val isAndroidTest = plugins.hasPlugin("com.android.test")
    val isAndroidInstantApp = plugins.hasPlugin("com.android.instantapp")
    return isAndroidLibrary || isAndroidApp || isAndroidTest || isAndroidInstantApp
}

private fun Project.isKotlinProject(): Boolean {
    val isKotlin = plugins.hasPlugin("kotlin")
    val isKotlinAndroid = plugins.hasPlugin("kotlin-android")
    val isKotlinMultiPlatform = plugins.hasPlugin("org.jetbrains.kotlin.multiplatform")
    val isKotlinPlatformCommon = plugins.hasPlugin("kotlin-platform-common")
    val isKotlinPlatformJvm = plugins.hasPlugin("kotlin-platform-jvm")
    val isKotlinPlatformJs = plugins.hasPlugin("kotlin-platform-js")
    return isKotlin || isKotlinAndroid || isKotlinMultiPlatform || isKotlinPlatformCommon || isKotlinPlatformJvm || isKotlinPlatformJs
}

internal fun Project.isNative() = requiredBooleanProperty("native")

var isCI = System.getenv().containsKey("CI") || System.getenv().containsKey("bamboo")

data class TrivyTaskData(
    val url: String,
    val username: String,
    val password: String,
)
