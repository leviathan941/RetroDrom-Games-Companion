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

package org.leviathan941.retrodromcompanion.ui.navigation

import androidx.annotation.Keep
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.leviathan941.retrodromcompanion.common.Constants
import org.leviathan941.retrodromcompanion.notification.RETRODROM_FEED_ITEM_QUERY_CHANNEL_URL

sealed interface AppDestination

sealed interface MainDestination : AppDestination {

    @Keep
    @Serializable
    data object RssFeed : MainDestination

    @Keep
    @Serializable
    data object Settings : MainDestination
}

sealed interface SettingsDestination : AppDestination {
    @Keep
    @Serializable
    data object Main : SettingsDestination

    @Keep
    @Serializable
    data object AppTheme : SettingsDestination

    @Keep
    @Serializable
    data object Notifications : SettingsDestination

    @Keep
    @Serializable
    data object Feedback : SettingsDestination

    @Keep
    @Serializable
    data object Licenses : SettingsDestination
}

sealed interface RssFeedDestination : AppDestination {
    @Keep
    @Serializable
    data class Feed(
        val id: Int,
        val title: String,
        val channelUrl: String,
    ) : RssFeedDestination

    @Keep
    @Serializable
    data class ItemDescription(
        val title: String,
        val link: String,
        val pubDate: String,
        val html: String,
        val creator: String?,
    ) : RssFeedDestination

    @Keep
    @Serializable
    data class LoadingItem(
        val postId: String,
        @SerialName(RETRODROM_FEED_ITEM_QUERY_CHANNEL_URL)
        val channelUrl: String = Constants.RETRODROM_BASE_URL,
    ) : RssFeedDestination
}
