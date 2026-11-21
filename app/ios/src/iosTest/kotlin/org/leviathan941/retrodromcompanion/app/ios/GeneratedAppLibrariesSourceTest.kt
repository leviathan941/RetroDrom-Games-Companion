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

package org.leviathan941.retrodromcompanion.app.ios

import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertTrue

class GeneratedAppLibrariesSourceTest {
    @Test
    fun listsTheLibrariesOfTheIosGraph() = runTest {
        val ids = generatedAppLibrariesSource.load().libraries.map { it.uniqueId }.toSet()

        listOf(
            "com.mikepenz:aboutlibraries-core",
            "dev.zacsweers.metro:runtime",
            "io.ktor:ktor-client-core",
            "org.jetbrains.compose.material3:material3",
        ).forEach { id ->
            assertTrue(id in ids, "$id is missing from $ids")
        }
    }
}
