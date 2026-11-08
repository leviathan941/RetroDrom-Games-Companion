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
    id("org.jetbrains.kotlin.multiplatform")
    id("com.android.kotlin.multiplatform.library")
    // The KMP library plugin registers no Android lint task on its own.
    id("com.android.lint")
}

kotlin {
    explicitApi = ExplicitApiMode.Strict

    android {
        compileSdk = AndroidSdk.COMPILE_SDK_VERSION
        minSdk = AndroidSdk.MIN_SDK_VERSION
        withHostTest {}
        compilerOptions {
            jvmTarget = JvmTarget.fromTarget(JvmVersions.KOTLIN_JVM_TARGET)
        }
        lint {
            warningsAsErrors = true
        }
    }
    iosArm64()
    iosSimulatorArm64()
}

// Root `./gradlew test` selects tasks named exactly `test`, and a KMP module has none.
tasks.register("test") {
    group = LifecycleBasePlugin.VERIFICATION_GROUP
    description = "Runs the Android host (JVM) unit tests."
    dependsOn("testAndroidHostTest")
}
