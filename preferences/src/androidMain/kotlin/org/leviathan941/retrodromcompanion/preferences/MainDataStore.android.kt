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

package org.leviathan941.retrodromcompanion.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.FileStorage
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.PreferencesFileSerializer
import androidx.datastore.preferences.preferencesDataStoreFile
import org.leviathan941.retrodromcompanion.preferences.internal.MAIN_DATA_STORE_NAME
import org.leviathan941.retrodromcompanion.preferences.internal.buildMainDataStore

/**
 * Creates the app's single preferences store, in `files/datastore/main_preferences.preferences_pb`.
 *
 * Call it once per process and share the instance: two stores on one file fail when read.
 */
public fun createMainDataStore(context: Context): DataStore<Preferences> = buildMainDataStore(
    storage = FileStorage(
        serializer = PreferencesFileSerializer,
        produceFile = { context.preferencesDataStoreFile(MAIN_DATA_STORE_NAME) },
    ),
)
