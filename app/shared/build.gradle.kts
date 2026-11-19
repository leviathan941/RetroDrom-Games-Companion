/*
 * RetroDrom Games Companion
 * Copyright (C) 2026 Alexey Kuzin <amkuzink@gmail.com>.
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
    id("retrodrom.kmp-library")
    // Stability inference for the state classes and navigation keys :app's composables take.
    alias(libs.plugins.jetbrains.kotlin.compose.compiler)
    alias(libs.plugins.jetbrains.kotlin.serialization)
    alias(libs.plugins.zacsweers.metro)
}

kotlin {
    android {
        namespace = "org.leviathan941.retrodromcompanion.shared"

        optimization {
            consumerKeepRules.publish = true
            consumerKeepRules.file("consumer-rules.pro")
        }
    }
    sourceSets {
        commonMain.dependencies {
            // Libs is part of the API: LicensesViewModel exposes it, :app binds AppLibrariesSource.
            api(libs.aboutlibraries.core)

            implementation(project(":common"))
            implementation(project(":network:cache"))
            implementation(project(":notification"))
            implementation(project(":preferences"))
            implementation(project(":resources"))
            implementation(project(":rss-reader"))

            implementation(libs.androidx.navigation3.runtime)
            implementation(libs.jetbrains.compose.runtime)
            implementation(libs.jetbrains.kotlinx.collections.immutable)
            implementation(libs.jetbrains.kotlinx.coroutines.core)
            implementation(libs.jetbrains.kotlinx.serialization.json)
            implementation(libs.zacsweers.metrox.viewmodel)
        }
        commonTest.dependencies {
            implementation(libs.jetbrains.kotlin.test)
        }
    }
}
