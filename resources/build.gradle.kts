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
    alias(libs.plugins.jetbrains.compose)
    // The CMP plugin refuses to configure without it, though the module has no composables.
    alias(libs.plugins.jetbrains.kotlin.compose.compiler)
}

kotlin {
    android {
        namespace = "org.leviathan941.retrodromcompanion.resources"
        // For the push resources Android reads itself; the UI ones are compose resources.
        androidResources {
            enable = true
        }
    }
    sourceSets {
        commonMain.dependencies {
            api(libs.jetbrains.compose.components.resources)
            // The Compose compiler insists on it; the resources artifact keeps it off the compile classpath on Android.
            implementation(libs.jetbrains.compose.runtime)
        }
    }
}

compose.resources {
    publicResClass = true
    packageOfResClass = "org.leviathan941.retrodromcompanion.resources"
    generateResClass = always
}
