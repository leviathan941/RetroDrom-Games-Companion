/*
 * Copyright 2026 Alexey Kuzin <amkuzink@gmail.com>
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

import org.jetbrains.kotlin.gradle.dsl.ExplicitApiMode
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.leviathan941.retrodromcompanion.AndroidSdk
import org.leviathan941.retrodromcompanion.JvmVersions

plugins {
    id("com.android.library")
}

kotlin {
    explicitApi = ExplicitApiMode.Strict
    compilerOptions {
        jvmTarget = JvmTarget.fromTarget(JvmVersions.KOTLIN_JVM_TARGET)
    }
}

android {
    compileSdk = AndroidSdk.COMPILE_SDK_VERSION

    defaultConfig {
        minSdk = AndroidSdk.MIN_SDK_VERSION
    }

    compileOptions {
        sourceCompatibility = JvmVersions.JAVA_SOURCE_COMPATIBILITY
        targetCompatibility = JvmVersions.JAVA_SOURCE_COMPATIBILITY
    }

    lint {
        warningsAsErrors = true
    }
}
