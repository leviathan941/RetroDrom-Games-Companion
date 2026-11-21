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

import com.skydoves.compose.stability.gradle.StabilityCheckTask
import com.skydoves.compose.stability.gradle.StabilityDumpTask

plugins {
    id("retrodrom.kmp-library")
    alias(libs.plugins.jetbrains.kotlin.compose.compiler)
    alias(libs.plugins.jetbrains.kotlin.serialization)
    alias(libs.plugins.skydoves.compose.stability.analyzer)
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
            // Libs are part of the API: LicensesViewModel exposes it, :app binds AppLibrariesSource.
            api(libs.aboutlibraries.core)

            implementation(project(":app:migration"))
            implementation(project(":browser"))
            implementation(project(":common"))
            implementation(project(":html-text"))
            implementation(project(":html-text:imagecontent"))
            implementation(project(":network"))
            implementation(project(":network:cache"))
            implementation(project(":notification"))
            implementation(project(":permission"))
            implementation(project(":preferences"))
            implementation(project(":resources"))
            implementation(project(":rss-reader"))

            implementation(libs.aboutlibraries.compose.m3)
            implementation(libs.androidx.lifecycle.viewmodel.navigation3)
            implementation(libs.androidx.navigation3.runtime)
            implementation(libs.androidx.paging.compose)
            implementation(libs.coil3.coil)
            implementation(libs.coil3.compose)
            implementation(libs.coil3.network.ktor3)
            implementation(libs.jetbrains.androidx.navigation3.ui)
            implementation(libs.jetbrains.compose.material.icons.core)
            implementation(libs.jetbrains.compose.material3)
            implementation(libs.jetbrains.compose.runtime)
            implementation(libs.jetbrains.compose.ui)
            implementation(libs.jetbrains.compose.ui.tooling.preview)
            implementation(libs.jetbrains.kotlinx.collections.immutable)
            implementation(libs.jetbrains.kotlinx.coroutines.core)
            implementation(libs.jetbrains.kotlinx.serialization.json)
            implementation(project.dependencies.platform(libs.ktor.bom))
            implementation(libs.ktor.client.core)
            implementation(libs.zacsweers.metrox.viewmodel)
            implementation(libs.zacsweers.metrox.viewmodel.compose)
        }
        androidMain.dependencies {
            implementation(project(":firebase"))

            implementation(libs.androidx.core.ktx)
            implementation(libs.androidx.datastore.preferences)
            implementation(libs.androidx.lifecycle.process)
        }
        commonTest.dependencies {
            implementation(libs.jetbrains.kotlin.test)
        }
    }
}

// The stability tasks read the first stability-info.json among the KMP compilations, and the
// Android and iOS ones differ: native spells defaulted parameter types nullable and infers
// NavBackStack unstable. Pin both tasks to the Android compilation, the one the app ships.
val androidStabilityInfo =
    layout.buildDirectory.file("stability/compileAndroidMain/stability-info.json")
tasks.withType<StabilityDumpTask>().configureEach {
    stabilityInputFiles.setFrom(androidStabilityInfo)
    dependsOn("compileAndroidMain")
}
tasks.withType<StabilityCheckTask>().configureEach {
    stabilityInputFiles.setFrom(androidStabilityInfo)
    dependsOn("compileAndroidMain")
}

dependencies {
    // Renders @Preview in the IDE; the KMP Android target has no debug variant.
    androidRuntimeClasspath(libs.jetbrains.compose.ui.tooling)

    lintChecks(libs.slack.compose.lint.checks)
}
