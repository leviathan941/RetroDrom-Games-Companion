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

package org.leviathan941.retrodromcompanion.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.leviathan941.retrodromcompanion.preferences.internal.APP_THEME_PREFERENCE_KEY
import org.leviathan941.retrodromcompanion.preferences.internal.SUBSCRIBED_PUSH_TOPICS

public class PreferencesRepository(
    private val dataStore: DataStore<Preferences>,
) {
    public val ui: Flow<UiPreferences> = dataStore.data
        .map { preferences ->
            UiPreferences(
                appTheme = preferences[APP_THEME_PREFERENCE_KEY],
                subscribedPushTopics = preferences[SUBSCRIBED_PUSH_TOPICS] ?: emptySet(),
            )
        }

    public suspend fun setAppTheme(appTheme: String) {
        dataStore.edit { preferences ->
            preferences[APP_THEME_PREFERENCE_KEY] = appTheme
        }
    }

    public suspend fun addPushTopicSubscription(topicName: String) {
        dataStore.edit { preferences ->
            preferences[SUBSCRIBED_PUSH_TOPICS] =
                preferences[SUBSCRIBED_PUSH_TOPICS].orEmpty() + topicName
        }
    }

    public suspend fun removePushTopicSubscription(topicName: String) {
        dataStore.edit { preferences ->
            preferences[SUBSCRIBED_PUSH_TOPICS]?.let { currentTopics ->
                preferences[SUBSCRIBED_PUSH_TOPICS] = currentTopics - topicName
            }
        }
    }
}
