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

package org.leviathan941.retrodromcompanion.preferences.internal

import androidx.datastore.core.DataMigration
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey

internal class Preference0To1Migration : DataMigration<Preferences> {
    private val newsPushEnabledPreferenceKey = booleanPreferencesKey("news_push_enabled")

    override suspend fun cleanUp() = Unit

    override suspend fun shouldMigrate(currentData: Preferences): Boolean {
        return currentData.contains(newsPushEnabledPreferenceKey)
    }

    override suspend fun migrate(currentData: Preferences): Preferences {
        return currentData.toMutablePreferences().also { prefs ->
            if (newsPushEnabledPreferenceKey in prefs) {
                prefs[SUBSCRIBED_PUSH_TOPICS] = setOf("new_retrodrom_posts")
                prefs.remove(newsPushEnabledPreferenceKey)
            }
        }
    }
}
