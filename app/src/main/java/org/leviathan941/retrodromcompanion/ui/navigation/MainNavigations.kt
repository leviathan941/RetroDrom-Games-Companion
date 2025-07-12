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

import android.util.Log
import androidx.annotation.Keep
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.toRoute
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.leviathan941.retrodromcompanion.common.Constants
import org.leviathan941.retrodromcompanion.notification.RETRODROM_FEED_ITEM_QUERY_CHANNEL_URL
import org.leviathan941.retrodromcompanion.ui.MAIN_VIEW_TAG

sealed interface AppDestination

sealed interface MainDestination : AppDestination {
    @Keep
    @Serializable
    data object Loading : MainDestination

    @Keep
    @Serializable
    data object RssFeed : MainDestination

    @Keep
    @Serializable
    data object SomethingWentWrong : MainDestination

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

data class MainNavPredefinedDestinations(
    val rssFeedStart: RssFeedDestination.Feed,
)

class MainNavActions(
    private val navController: NavController,
    private val predefinedDestinations: MainNavPredefinedDestinations,
) {
    fun navigateBack() {
        navController.popBackStack()
    }

    fun navigateToLoading() {
        Log.d(MAIN_VIEW_TAG, "Navigate to loading screen")
        navController.navigate(MainDestination.Loading) {
            popUpTo(MainDestination.Loading) {
                inclusive = true
            }
            launchSingleTop = true
        }
    }

    fun navigateToRssFeed(
        destination: RssFeedDestination.Feed = predefinedDestinations.rssFeedStart
    ) {
        Log.d(MAIN_VIEW_TAG, "Navigate to RSS feed screen: $destination")
        val isInsideRssFeed = navController.currentDestination
            ?.hasRoute<RssFeedDestination.Feed>() == true
        val toSameFeed = navController.currentBackStackEntry?.takeIf { isInsideRssFeed }
            ?.toRoute<RssFeedDestination.Feed>() == destination
        if (toSameFeed) {
            Log.d(MAIN_VIEW_TAG, "Already on the same RSS feed screen")
            return
        }
        navController.navigate(destination) {
            if (!isInsideRssFeed) {
                popUpTo(MainDestination.Loading) {
                    inclusive = true
                }
            }
        }
    }

    fun navigateToRssItemDescription(
        destination: RssFeedDestination.ItemDescription,
    ) {
        Log.d(MAIN_VIEW_TAG, "Navigate to RSS item description screen")
        navController.navigate(destination) {
            launchSingleTop = true
        }
    }

    fun navigateToSettings() {
        Log.d(MAIN_VIEW_TAG, "Navigate to settings screen")
        navController.navigate(MainDestination.Settings) {
            launchSingleTop = true
        }
    }

    fun navigateToSettingsItem(destination: SettingsDestination) {
        Log.d(MAIN_VIEW_TAG, "Navigate to $destination settings screen")
        navController.navigate(destination) {
            launchSingleTop = true
        }
    }
}
