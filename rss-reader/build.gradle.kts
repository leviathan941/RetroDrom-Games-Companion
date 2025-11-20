/*
 * RetroDrom Games Companion
 * Copyright (C) 2024 Alexey Kuzin <amkuzink@gmail.com>.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.leviathan941.retrodromcompanion.AndroidSdk
import org.leviathan941.retrodromcompanion.JvmVersions

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.google.ksp)
}

kotlin {
    compilerOptions {
        jvmTarget = JvmTarget.fromTarget(JvmVersions.KOTLIN_JVM_TARGET)
    }
}

android {
    namespace = "org.leviathan941.retrodromcompanion.rssreader"
    compileSdk = AndroidSdk.COMPILE_SDK_VERSION

    buildFeatures {
        buildConfig = true
    }

    compileOptions {
        sourceCompatibility = JvmVersions.JAVA_SOURCE_COMPATIBILITY
        targetCompatibility = JvmVersions.JAVA_SOURCE_COMPATIBILITY
    }

    defaultConfig {
        minSdk = AndroidSdk.MIN_SDK_VERSION
    }
}

dependencies {
    api(libs.androidx.paging.compose)

    implementation(libs.androidx.core.ktx)
    implementation(libs.jetbrains.kotlinx.collections.immutable)
    implementation(libs.jetbrains.kotlinx.coroutines.android)

    implementation(libs.ktrssreader.android)
    implementation(libs.ktrssreader.annotation)
    ksp(libs.ktrssreader.processor)
}
