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

import com.mikepenz.aboutlibraries.Libs
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.leviathan941.retrodromcompanion.app.licenses.AppLibrariesSource

// Exported by the AboutLibraries plugin from this module's classpath, the whole iOS app's.
private const val LIBRARIES_FILE = "files/aboutlibraries.json"

internal val generatedAppLibrariesSource = AppLibrariesSource {
    val json = Res.readBytes(LIBRARIES_FILE).decodeToString()
    withContext(Dispatchers.Default) {
        Libs.Builder().withJson(json).build()
    }
}
