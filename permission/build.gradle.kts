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
    id("retrodrom.android-library")
    alias(libs.plugins.jetbrains.kotlin.compose.compiler)
}

android {
    namespace = "org.leviathan941.retrodromcompanion.permission"
}

dependencies {
    implementation(libs.mohamedrejeb.calf.permissions.core)
    implementation(libs.mohamedrejeb.calf.permissions.notifications)
    implementation(libs.jetbrains.compose.material.icons.core)
    implementation(libs.jetbrains.compose.material3)
    implementation(libs.jetbrains.compose.ui)
    implementation(libs.jetbrains.compose.ui.tooling.preview)

    debugImplementation(libs.jetbrains.compose.ui.tooling)

    lintChecks(libs.slack.compose.lint.checks)
}
