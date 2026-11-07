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

import org.leviathan941.retrodromcompanion.Application

plugins {
    id("retrodrom.android-application")
    alias(libs.plugins.jetbrains.kotlin.compose.compiler)
    alias(libs.plugins.google.services)
    alias(libs.plugins.jetbrains.kotlin.serialization)
    alias(libs.plugins.aboutlibraries.android)
    alias(libs.plugins.skydoves.compose.stability.analyzer)
    alias(libs.plugins.zacsweers.metro)
}

android {
    namespace = "org.leviathan941.retrodromcompanion"

    defaultConfig {
        applicationId = Application.ID
        versionCode = Application.version.code
        versionName = Application.version.name

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }

        base.archivesName.set("${Application.BASE_NAME}-${Application.version.name}")
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
            ndk {
                debugSymbolLevel = "FULL"
            }
        }
        getByName("debug") {
            versionNameSuffix = "-SNAPSHOT"
        }
    }

    lint {
        checkDependencies = true
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    testOptions {
        // The navigation actions log through Kermit, whose Android writer calls android.util.Log.
        unitTests.isReturnDefaultValues = true
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
    implementation(project(":app:migration"))
    implementation(project(":browser"))
    implementation(project(":common"))
    implementation(project(":firebase"))
    implementation(project(":html-text"))
    implementation(project(":html-text:imagecontent"))
    implementation(project(":network"))
    implementation(project(":network:cache"))
    implementation(project(":notification"))
    implementation(project(":permission"))
    implementation(project(":preferences"))
    implementation(project(":rss-reader"))

    implementation(libs.aboutlibraries.compose.m3)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.androidx.lifecycle.process)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)

    implementation(libs.google.material)

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material.icons.core.android)
    implementation(libs.androidx.compose.material3)

    implementation(libs.androidx.activity.compose)

    implementation(libs.androidx.lifecycle.viewmodel.navigation3)
    implementation(libs.androidx.navigation3.runtime)
    implementation(libs.androidx.navigation3.ui)

    implementation(libs.coil3.compose)
    implementation(libs.coil3.network.ktor3)

    implementation(platform(libs.ktor.bom))
    implementation(libs.ktor.client.core)

    implementation(libs.androidx.datastore.preferences)

    implementation(libs.jetbrains.kotlinx.collections.immutable)
    implementation(libs.jetbrains.kotlinx.serialization.json)

    implementation(libs.zacsweers.metrox.viewmodel)
    implementation(libs.zacsweers.metrox.viewmodel.compose)

    debugImplementation(libs.compose.ui.tooling)

    testImplementation(libs.junit)

    lintChecks(libs.slack.compose.lint.checks)
}
