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

plugins {
    // Gives the root project check/build, so `build` lints the root scripts, and clean.
    base
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.jetbrains.kotlin.parcelize) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.android.kotlin.multiplatform.library) apply false
    alias(libs.plugins.android.lint) apply false
    alias(libs.plugins.jetbrains.kotlin.multiplatform) apply false
    alias(libs.plugins.jetbrains.compose) apply false
    alias(libs.plugins.google.ksp) apply false
    alias(libs.plugins.jetbrains.kotlin.serialization) apply false
    alias(libs.plugins.jetbrains.kotlin.compose.compiler) apply false
    alias(libs.plugins.google.services) apply false
    alias(libs.plugins.aboutlibraries.android) apply false
    alias(libs.plugins.androidx.room3) apply false
    alias(libs.plugins.skydoves.compose.stability.analyzer) apply false
    alias(libs.plugins.zacsweers.metro) apply false
    alias(libs.plugins.jlleitschuh.ktlint)
    alias(libs.plugins.detekt)
}

// Subprojects get their `libs` extension too late for allprojects, so read the catalog here.
val ktlintPluginId = libs.plugins.jlleitschuh.ktlint.get().pluginId
val ktlintVersion = libs.versions.pinterest.ktlint
val composeRules = libs.nlopez.compose.rules.ktlint
val detektPluginId = libs.plugins.detekt.get().pluginId
val composeRulesDetekt = libs.nlopez.compose.rules.detekt
val detektConfig = files("detekt.yaml")

allprojects {
    pluginManager.apply(ktlintPluginId)

    ktlint {
        version = ktlintVersion
        // The compose resources generator adds its output to the KMP source sets.
        filter {
            exclude {
                it.file.path.contains(
                    "${File.separator}build${File.separator}generated${File.separator}",
                )
            }
        }
    }

    dependencies {
        // Honors the ktlint_compose_* keys in .editorconfig.
        ktlintRuleset(composeRules)
    }

    pluginManager.apply(detektPluginId)

    detekt {
        // The default covers only src/{main,test}; this adds KMP and other Android source sets.
        source.setFrom("src")
        config.from(detektConfig)
        buildUponDefaultConfig = true
    }

    dependencies {
        detektPlugins(composeRulesDetekt)
    }
}
