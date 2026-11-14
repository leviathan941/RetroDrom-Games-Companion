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
    id("retrodrom.kmp-library")
    alias(libs.plugins.androidx.room3)
    alias(libs.plugins.google.ksp)
    alias(libs.plugins.zacsweers.metro)
}

kotlin {
    compilerOptions {
        // RoomFeedDatabaseConstructor is an `expect object`, still Beta in Kotlin.
        freeCompilerArgs.add("-Xexpect-actual-classes")
    }
    android {
        namespace = "org.leviathan941.retrodromcompanion.network.cache"
    }
    sourceSets {
        commonMain.dependencies {
            api(libs.androidx.paging.common)
            api(libs.jetbrains.kotlinx.collections.immutable)
            api(libs.jetbrains.kotlinx.coroutines.core)

            implementation(project(":common"))
            implementation(project(":network"))

            implementation(libs.androidx.room3.paging)
            implementation(libs.androidx.room3.runtime)
            implementation(libs.androidx.sqlite.bundled)
        }
        commonTest.dependencies {
            implementation(libs.jetbrains.kotlin.test)
            implementation(libs.jetbrains.kotlinx.coroutines.test)
        }
    }
}

room3 {
    schemaDirectory("$projectDir/schemas")
}

// Room generates the database for each target, including the `actual` of its constructor.
dependencies {
    add("kspAndroid", libs.androidx.room3.compiler)
    add("kspIosArm64", libs.androidx.room3.compiler)
    add("kspIosSimulatorArm64", libs.androidx.room3.compiler)
}
