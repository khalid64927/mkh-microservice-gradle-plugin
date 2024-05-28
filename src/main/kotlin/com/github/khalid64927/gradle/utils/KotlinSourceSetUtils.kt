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
package com.github.khalid64927.gradle.utils

import org.gradle.api.NamedDomainObjectContainer
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet

fun NamedDomainObjectContainer<KotlinSourceSet>.connectTargetsToSourceSet(
    targetNames: List<String>,
    sourceSetPrefix: String,
) {
    targetNames.forEach { setupDependency(name = it, dependencyName = sourceSetPrefix) }
}

fun NamedDomainObjectContainer<KotlinSourceSet>.setupDependency(name: String, dependencyName: String) {
    getByName("${name}Main").dependsOn(getByName("${dependencyName}Main"))
    getByName("${name}Test").dependsOn(getByName("${dependencyName}Test"))
}

fun NamedDomainObjectContainer<KotlinSourceSet>.createMainTest(name: String) {
    create("${name}Main")
    create("${name}Test")
}
