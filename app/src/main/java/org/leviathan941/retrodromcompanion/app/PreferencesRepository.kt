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

package org.leviathan941.retrodromcompanion.app

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.leviathan941.retrodromcompanion.ui.APP_THEME_DEFAULT
import org.leviathan941.retrodromcompanion.ui.theme.ThemeType

class PreferencesRepository(
    private val dataStore: DataStore<Preferences>,
) {
    val uiPreferences: Flow<UiPreferences> = dataStore.data
        .map { preferences ->
            UiPreferences(
                appTheme = preferences[APP_THEME_PREFERENCE_KEY]?.let {
                    ThemeType.fromValue(it)
                } ?: APP_THEME_DEFAULT,
            )
        }

    suspend fun setAppTheme(appTheme: ThemeType) {
        dataStore.edit { preferences ->
            preferences[APP_THEME_PREFERENCE_KEY] = appTheme.value
        }
    }
}
