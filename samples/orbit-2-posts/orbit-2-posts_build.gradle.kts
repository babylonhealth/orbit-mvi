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
    id("com.android.application")
    kotlin("android")
    kotlin("kapt")
    id("kotlin-android-extensions")
    id("androidx.navigation.safeargs.kotlin")
}

android {
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    compileSdkVersion(29)
    defaultConfig {
        minSdkVersion(21)
        targetSdkVersion(29)
        applicationId = "com.babylon.orbit2.sample.posts"
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation(project(":orbit-2-core"))
    implementation(project(":orbit-2-coroutines"))
    implementation(project(":orbit-2-livedata"))
    implementation(project(":orbit-2-viewmodel"))

    // Kotlin
    implementation(ProjectDependencies.kotlinCoroutines)

    // UI
    implementation(ProjectDependencies.androidxAppCompat)
    implementation("androidx.vectordrawable:vectordrawable:1.1.0")
    implementation(ProjectDependencies.androidxAnnotation)
    implementation("androidx.recyclerview:recyclerview:1.1.0")
    implementation(ProjectDependencies.androidxConstrainLayout)
    implementation("androidx.core:core-ktx:1.3.1")
    implementation("androidx.arch.core:core-runtime:2.1.0")
    implementation("androidx.arch.core:core-common:2.1.0")
    implementation("androidx.lifecycle:lifecycle-extensions:2.2.0")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.2.0")
    implementation("androidx.paging:paging-runtime-ktx:2.1.2")
    implementation("com.google.android.material:material:1.1.0")
    implementation("com.github.bumptech.glide:glide:4.11.0")
    implementation(ProjectDependencies.groupie)
    implementation(ProjectDependencies.groupieKotlinAndroidExtensions)
    implementation(ProjectDependencies.androidxNavigationFragmentKtx)
    implementation(ProjectDependencies.androidxNavigationUiKtx)

    // Networking
    implementation("com.squareup.okhttp3:okhttp:4.8.0")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-jackson:2.9.0")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.11.1")
    testImplementation("com.squareup.retrofit2:retrofit-mock:2.9.0")

    // Testing
    testImplementation(project(":orbit-2-test"))
    GroupedDependencies.testsImplementation.forEach { testImplementation(it) }
    GroupedDependencies.testsRuntime.forEach { testRuntimeOnly(it) }
    testImplementation("androidx.test:core-ktx:1.2.0")
    testImplementation(ProjectDependencies.androidXTesting)
    testImplementation("org.robolectric:robolectric:4.3.1")

    // Debugging
    debugImplementation("com.squareup.leakcanary:leakcanary-android:2.4")
    implementation("com.squareup.leakcanary:plumber-android:2.4")

    // Dependency Injection
    implementation("org.koin:koin-androidx-viewmodel:2.1.6")

    // Database
    implementation("androidx.room:room-runtime:2.2.5")
    implementation("androidx.room:room-ktx:2.2.5")
    kapt("androidx.room:room-compiler:2.2.5")
    testImplementation("androidx.room:room-testing:2.2.5")

    implementation("com.android.support.constraint:constraint-layout:1.1.3")
}
