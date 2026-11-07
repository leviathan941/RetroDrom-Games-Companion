/*
 * RetroDrom Games Companion
 * Copyright (C) 2025 Alexey Kuzin <amkuzink@gmail.com>.
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
    alias(libs.plugins.androidx.room3)
    alias(libs.plugins.google.ksp)
    alias(libs.plugins.zacsweers.metro)
}

android {
    namespace = "org.leviathan941.retrodromcompanion.network.cache"

    room3 {
        schemaDirectory("$projectDir/schemas")
    }
}

dependencies {
    api(libs.androidx.paging.common)
    api(libs.jetbrains.kotlinx.collections.immutable)
    api(libs.jetbrains.kotlinx.coroutines.android)

    implementation(project(":common"))
    implementation(project(":network"))

    implementation(libs.androidx.room3.paging)
    implementation(libs.androidx.room3.runtime)
    implementation(libs.androidx.sqlite.bundled)

    ksp(libs.androidx.room3.compiler)
}
