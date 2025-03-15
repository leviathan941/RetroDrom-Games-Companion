/*
 * Copyright 2024 Alexey Kuzin <amkuzink@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.leviathan941.retrodromcompanion.AndroidSdk
import org.leviathan941.retrodromcompanion.Application
import org.leviathan941.retrodromcompanion.JvmVersions

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.dagger.hilt.android)
    alias(libs.plugins.google.services)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.ksp)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "org.leviathan941.retrodromcompanion"
    compileSdk = AndroidSdk.COMPILE_SDK_VERSION

    defaultConfig {
        applicationId = Application.ID
        minSdk = AndroidSdk.MIN_SDK_VERSION
        targetSdk = AndroidSdk.TARGET_SDK_VERSION
        versionCode = Application.version.code
        versionName = Application.version.name

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }

        setProperty("archivesBaseName", "${Application.BASE_NAME}-${Application.version.name}")
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            ndk {
                debugSymbolLevel = "FULL"
            }
        }
        getByName("debug") {
            versionNameSuffix = "-SNAPSHOT"
        }
    }
    compileOptions {
        sourceCompatibility = JvmVersions.JAVA_SRC_COMPAT
        targetCompatibility = JvmVersions.JAVA_TARGET_COMPAT
    }
    kotlin {
        jvmToolchain(JvmVersions.KOTLIN_JVM)
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    packaging {
        resources {
            excludes += setOf(
                "/META-INF/{AL2.0,LGPL2.1}",
                "DebugProbesKt.bin",
            )
        }
    }
}

dependencies {
    implementation(project(":common"))
    implementation(project(":firebase"))
    implementation(project(":html-text"))
    implementation(project(":html-text:imagecontent"))
    implementation(project(":network"))
    implementation(project(":notification"))
    implementation(project(":permission"))
    implementation(project(":preferences"))
    implementation(project(":rss-reader"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.browser)
    implementation(libs.androidx.lifecycle.process)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)

    implementation(libs.google.material)

    implementation(libs.compose.ui)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.compose.material3)

    implementation(libs.activity.compose)

    implementation(libs.navigation.compose)

    implementation(libs.coil.compose)

    implementation(libs.datastore.preferences)

    implementation(libs.kotlinx.serialization.json)

    implementation(libs.accompanist.permissions)

    implementation(libs.dagger.hilt.android)
    implementation(libs.androidx.hilt.navigation.compose)
    ksp(libs.dagger.hilt.compiler)

    debugImplementation(libs.compose.ui.tooling)
}
