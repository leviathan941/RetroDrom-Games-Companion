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

package org.leviathan941.retrodromcompanion.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.core.okio.OkioStorage
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.PreferencesSerializer
import kotlinx.cinterop.ExperimentalForeignApi
import okio.FileSystem
import okio.Path.Companion.toPath
import org.leviathan941.retrodromcompanion.preferences.internal.MAIN_DATA_STORE_FILE_EXTENSION
import org.leviathan941.retrodromcompanion.preferences.internal.MAIN_DATA_STORE_NAME
import org.leviathan941.retrodromcompanion.preferences.internal.buildMainDataStore
import platform.Foundation.NSApplicationSupportDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask

/**
 * Creates the app's single preferences store, in the app's Application Support directory.
 *
 * Call it once per process and share the instance: two stores on one file fail when read.
 */
public fun createMainDataStore(): DataStore<Preferences> = buildMainDataStore(
    storage = OkioStorage(
        fileSystem = FileSystem.SYSTEM,
        serializer = PreferencesSerializer,
        producePath = {
            applicationSupportDirectory().toPath() /
                "$MAIN_DATA_STORE_NAME$MAIN_DATA_STORE_FILE_EXTENSION"
        },
    ),
)

// Application Support, unlike Documents, is hidden from the user and does not exist until created.
@OptIn(ExperimentalForeignApi::class)
private fun applicationSupportDirectory(): String {
    val url = NSFileManager.defaultManager.URLForDirectory(
        directory = NSApplicationSupportDirectory,
        inDomain = NSUserDomainMask,
        appropriateForURL = null,
        create = true,
        error = null,
    )
    return requireNotNull(url?.path) { "No Application Support directory" }
}
