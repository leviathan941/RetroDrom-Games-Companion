import org.jetbrains.kotlin.gradle.dsl.ExplicitApiMode
import org.leviathan941.retrodromcompanion.AndroidSdk
import org.leviathan941.retrodromcompanion.JvmVersions

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

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "org.leviathan941.retrodromcompanion.permission"
    compileSdk = AndroidSdk.COMPILE_SDK_VERSION

    defaultConfig {
        minSdk = AndroidSdk.MIN_SDK_VERSION
    }

    compileOptions {
        sourceCompatibility = JvmVersions.JAVA_SRC_COMPAT
        targetCompatibility = JvmVersions.JAVA_TARGET_COMPAT
    }
    kotlin {
        jvmToolchain(JvmVersions.KOTLIN_JVM)
        explicitApi = ExplicitApiMode.Strict
    }
}

dependencies {
    implementation(libs.accompanist.permissions)
    implementation(libs.compose.material3)
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.tooling.preview)

    debugImplementation(libs.compose.ui.tooling)
}
