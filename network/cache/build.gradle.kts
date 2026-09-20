/*
 * RetroDrom Games Companion
 * Copyright (C) 2025 Alexey Kuzin <amkuzink@gmail.com>.
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

import org.jetbrains.kotlin.gradle.dsl.ExplicitApiMode
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.leviathan941.retrodromcompanion.AndroidSdk
import org.leviathan941.retrodromcompanion.JvmVersions

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.androidx.room3)
    alias(libs.plugins.google.ksp)
    alias(libs.plugins.zacsweers.metro)
}

kotlin {
    compilerOptions {
        jvmTarget = JvmTarget.fromTarget(JvmVersions.KOTLIN_JVM_TARGET)
    }
}

android {
    namespace = "org.leviathan941.retrodromcompanion.network.cache"
    compileSdk = AndroidSdk.COMPILE_SDK_VERSION

    defaultConfig {
        minSdk = AndroidSdk.MIN_SDK_VERSION
    }

    kotlin {
        explicitApi = ExplicitApiMode.Strict
    }

    compileOptions {
        sourceCompatibility = JvmVersions.JAVA_SOURCE_COMPATIBILITY
        targetCompatibility = JvmVersions.JAVA_SOURCE_COMPATIBILITY
    }

    lint {
        warningsAsErrors = true
    }

    room3 {
        schemaDirectory("$projectDir/schemas")
    }
}

dependencies {
    api(libs.androidx.paging.common)
    api(libs.jetbrains.kotlinx.collections.immutable)
    api(libs.jetbrains.kotlinx.coroutines.android)

    implementation(project(":common"))
    implementation(project(":network"))

    implementation(libs.androidx.room3.paging)
    implementation(libs.androidx.room3.runtime)
    implementation(libs.androidx.sqlite.bundled)

    ksp(libs.androidx.room3.compiler)
}
