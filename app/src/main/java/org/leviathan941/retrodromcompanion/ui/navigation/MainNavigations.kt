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
import androidx.compose.runtime.Stable
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.deeplink.DeepLinkRequest
import androidx.navigation3.runtime.deeplink.DeepLinkUri
import androidx.navigation3.runtime.deeplink.UriDeepLinkMatcher
import androidx.savedstate.serialization.SavedStateConfiguration
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import org.leviathan941.retrodromcompanion.common.Constants
import org.leviathan941.retrodromcompanion.notification.RETRODROM_FEED_ITEM_BASE_PATH
import org.leviathan941.retrodromcompanion.notification.RETRODROM_FEED_ITEM_QUERY_CHANNEL_URL

@Serializable
sealed interface AppDestination : NavKey

/**
 * Saves the back stack by registering every [AppDestination] explicitly, which non-Android targets
 * require: Android's reflection-based alternative is not multiplatform.
 */
@OptIn(ExperimentalSerializationApi::class)
val appDestinationSavedStateConfiguration = SavedStateConfiguration {
    serializersModule = SerializersModule {
        polymorphic(NavKey::class) {
            subclassesOfSealed<AppDestination>()
        }
    }
}

private val feedItemDeepLinkMatcher = UriDeepLinkMatcher(
    uriPattern = DeepLinkUri(
        "$RETRODROM_FEED_ITEM_BASE_PATH/{postId}" +
            "?$RETRODROM_FEED_ITEM_QUERY_CHANNEL_URL={$RETRODROM_FEED_ITEM_QUERY_CHANNEL_URL}",
    ),
    serializer = RssFeedDestination.LoadingItem.serializer(),
)

/** Returns the destination [deeplink] opens, or `null` when it matches none. */
fun matchDeepLink(deeplink: String): RssFeedDestination.LoadingItem? =
    feedItemDeepLinkMatcher.match(DeepLinkRequest(deeplink))?.key

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
    @Stable
    @Keep
    @Serializable
    data class Feed(
        val id: Int,
        val title: String,
        val channelUrl: String,
    ) : RssFeedDestination

    @Stable
    @Keep
    @Serializable
    data class ItemDescription(
        val title: String,
        val link: String,
        val pubDate: String,
        val html: String,
        val creator: String?,
    ) : RssFeedDestination

    @Stable
    @Keep
    @Serializable
    data class LoadingItem(
        val postId: String,
        @SerialName(RETRODROM_FEED_ITEM_QUERY_CHANNEL_URL)
        val channelUrl: String = Constants.RETRODROM_BASE_URL,
    ) : RssFeedDestination
}
