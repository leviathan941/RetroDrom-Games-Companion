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

package org.leviathan941.retrodromcompanion.preferences.internal

import androidx.datastore.core.DataStore
import androidx.datastore.core.Storage
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences

/**
 * The main store's name. Android derives its file from it with `preferencesDataStoreFile`,
 * which appends [MAIN_DATA_STORE_FILE_EXTENSION].
 */
internal const val MAIN_DATA_STORE_NAME = "main_preferences"
internal const val MAIN_DATA_STORE_FILE_EXTENSION = ".preferences_pb"

/**
 * Builds the main store on the platform's [storage], with the store's migrations.
 */
internal fun buildMainDataStore(storage: Storage<Preferences>): DataStore<Preferences> =
    PreferenceDataStoreFactory.create(
        storage = storage,
        migrations = listOf(
            Preference0To1Migration(),
        ),
    )
