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
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.9-native-mt-2")
                kotlin("stdlib-native")
//                implementation("org.jetbrains.kotlinx:atomicfu-common:0.14.4")
            }
        }
        commonTest {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
//                implementation("io.kotest:kotest-assertions-core:4.2.3")
//                implementation(ProjectDependencies.kotlinCoroutinesTest)
                implementation(project(":orbit-2-test"))
            }
        }

        val jvmMain by getting {
            dependencies {
//                implementation(kotlin("stdlib-jdk7"))
            }
        }
        val jvmTest by getting {
//            dependsOn(commonTest.get())
            dependencies {
                implementation(kotlin("test"))
                implementation(kotlin("test-junit5"))
//                implementation("io.kotlintest:kotlintest-runner-junit5:3.4.2")
                runtimeOnly("org.junit.jupiter:junit-jupiter-engine:5.7.0")
//                implementation("org.jetbrains.kotlinx:atomicfu-native:0.14.4")
                implementation(ProjectDependencies.mockitoInline)
                implementation(ProjectDependencies.mockitoKotlin)

//                implementation(ProjectDependencies.kotlinCoroutinesTest)
//                GroupedDependencies.testsImplementation.forEach { implementation(it) }
//                runtimeOnly(ProjectDependencies.junitJupiterEngine)
            }
        }

        val iosMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.9-native-mt-2")
            }
        }

        val iosTest by getting {

        }
    }
}
//
//dependencies {
//    implementation(kotlin("stdlib-jdk8"))
//    implementation(ProjectDependencies.kotlinCoroutines)
//
//    compileOnly(ProjectDependencies.androidxAnnotation)
//
//    // Testing
//    testImplementation(project(":orbit-2-test"))
//    testImplementation(ProjectDependencies.kotlinCoroutinesTest)
//    GroupedDependencies.testsImplementation.forEach { testImplementation(it) }
//    testRuntimeOnly(ProjectDependencies.junitJupiterEngine)
//}
//
//// Fix lack of source code when publishing pure Kotlin projects
//// See https://github.com/novoda/bintray-release/issues/262
//tasks.whenTaskAdded {
//    if (name == "generateSourcesJarForMavenPublication") {
//        this as Jar
//        from(sourceSets.main.get().allSource)
//    }
//}

