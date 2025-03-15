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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.leviathan941.retrodromcompanion.common.di.DiKeys
import org.leviathan941.retrodromcompanion.preferences.internal.APP_THEME_PREFERENCE_KEY
import org.leviathan941.retrodromcompanion.preferences.internal.PUSH_POSTS_PROMO_STARTS_UNTIL_SHOW
import org.leviathan941.retrodromcompanion.preferences.internal.PUSH_POSTS_PROMO_STARTS_UNTIL_SHOW_DEFAULT
import org.leviathan941.retrodromcompanion.preferences.internal.SUBSCRIBED_PUSH_TOPICS
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
public class PreferencesRepository @Inject constructor(
    @Named(DiKeys.MAIN_DATASTORE)
    dataStore: DataStore<Preferences>,
) {
    public val ui: Flow<UiPreferences> = dataStore.data
        .map { preferences ->
            UiPreferences(
                appTheme = preferences[APP_THEME_PREFERENCE_KEY],
                subscribedPushTopics = preferences[SUBSCRIBED_PUSH_TOPICS] ?: emptySet(),
            )
        }
    public val uiEditor: UiPreferencesEditor = UiPreferencesEditor(dataStore)

    public val promo: Flow<PromoPreferences> = dataStore.data
        .map { preferences ->
            val pushPostsPromoPreferences = PushPostsPromoPreferences(
                startsUntilShow = preferences[PUSH_POSTS_PROMO_STARTS_UNTIL_SHOW]
                    ?: PUSH_POSTS_PROMO_STARTS_UNTIL_SHOW_DEFAULT,
            )
            PromoPreferences(
                pushPosts = pushPostsPromoPreferences,
            )
        }
    public val promoEditor: PromoPreferencesEditor = PromoPreferencesEditor(dataStore)
}
