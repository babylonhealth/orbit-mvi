/*
 * Copyright 2020 Babylon Partners Limited
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
plugins {
    kotlin("multiplatform")
}
apply<kotlinx.atomicfu.plugin.gradle.AtomicFUGradlePlugin>()

kotlin {
    jvm()
    ios()
    sourceSets {
        commonMain {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.9")
                kotlin("stdlib-native")
                implementation(ProjectDependencies.kotlinTest)

                api(project(":orbit-2-core"))
            }
        }
        commonTest {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
                implementation("io.kotest:kotest-assertions-core:4.2.3")
            }
        }

        val iosMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlin:kotlin-stdlib-common:1.4.10")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.9-native-mt")
            }
        }

        val jvmMain by getting {
            dependencies {
                implementation(ProjectDependencies.mockitoKotlin)
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.9")
            }
        }
        val jvmTest by getting {
            dependencies {
                GroupedDependencies.testsImplementation.forEach { implementation(it) }
                runtimeOnly(ProjectDependencies.junitJupiterEngine)
            }
        }
    }
}
