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
    id("retrodrom.kmp-ios-library")
    alias(libs.plugins.jetbrains.compose)
    alias(libs.plugins.jetbrains.kotlin.compose.compiler)
    alias(libs.plugins.aboutlibraries)
}

kotlin {
    listOf(iosArm64(), iosSimulatorArm64()).forEach { target ->
        target.binaries.framework {
            baseName = "RetroDromCompanion"
            isStatic = true
        }
    }
    sourceSets {
        // In commonMain, though every target is iOS: the compose resources plugin generates `Res`
        // there, and only for a commonMain dependency on the resources library.
        commonMain.dependencies {
            implementation(project(":app:migration"))
            implementation(project(":app:shared"))

            implementation(libs.aboutlibraries.core)
            implementation(libs.androidx.lifecycle.runtime.compose)
            implementation(libs.jetbrains.compose.components.resources)
            implementation(libs.jetbrains.compose.runtime)
            implementation(libs.jetbrains.compose.ui)
            implementation(libs.zacsweers.metrox.viewmodel.compose)
        }
        iosTest.dependencies {
            implementation(libs.jetbrains.kotlin.test)
            implementation(libs.jetbrains.kotlinx.coroutines.test)
        }
    }
}

// A compose resources directory: the list is read at runtime as `files/aboutlibraries.json`.
val aboutLibrariesDir = layout.buildDirectory.dir("generated/aboutLibraries/ios")

// The plugin reads only this module's configurations, so the list covers the whole iOS app.
// `iosArm64CompileKlibraries` alone: the per-target export task never matches it, and the
// `iosArm64` metadata configurations would add the wasm-only `kotlinx-browser`.
aboutLibraries {
    collect {
        all = true
        filterVariants.add("iosArm64CompileKlibraries")
    }
    export {
        outputFile = aboutLibrariesDir.map { it.file("files/aboutlibraries.json") }
    }
}

compose.resources {
    packageOfResClass = "org.leviathan941.retrodromcompanion.app.ios"
    // Taken from the task provider, so iOS resource processing depends on the export.
    customDirectory(
        sourceSetName = "iosMain",
        directoryProvider = tasks.named("exportLibraryDefinitions").map { aboutLibrariesDir.get() },
    )
}
