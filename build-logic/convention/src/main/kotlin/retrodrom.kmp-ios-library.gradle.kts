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

// A KMP module with the iOS targets only. `retrodrom.kmp-library` always adds the Android one,
// which brings the SDK levels, the JVM target and Android lint; none of them applies here.
plugins {
    id("org.jetbrains.kotlin.multiplatform")
}

kotlin {
    explicitApi = ExplicitApiMode.Strict

    // The same iOS targets as `retrodrom.kmp-library`, so these modules can depend on those.
    iosArm64()
    iosSimulatorArm64()
}
