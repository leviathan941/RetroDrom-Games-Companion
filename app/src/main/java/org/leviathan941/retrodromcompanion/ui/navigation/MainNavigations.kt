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
import androidx.navigation.NavController
import kotlinx.serialization.Serializable
import org.leviathan941.retrodromcompanion.ui.MAIN_VIEW_TAG

sealed interface AppDestination

sealed interface MainDestination : AppDestination {
    @Serializable
    data object Loading : MainDestination

    @Serializable
    data object RssFeed : MainDestination

    @Serializable
    data object SomethingWentWrong : MainDestination

    @Serializable
    data object Settings : MainDestination
}

sealed interface SettingsDestination : AppDestination {
    @Serializable
    data object Main : SettingsDestination

    @Serializable
    data object AppTheme : SettingsDestination

    @Serializable
    data object Feedback : SettingsDestination
}

sealed interface RssFeedDestination : AppDestination {
    @Serializable
    data class Feed(
        val id: Int,
        val title: String,
        val channelUrl: String,
    )

    @Serializable
    data class ItemDescription(
        val title: String,
        val link: String,
        val pubDate: String,
        val categories: List<String>,
        val imageUrl: String?,
        val paragraphs: List<String>,
        val creator: String?,
    ) : RssFeedDestination
}

class MainNavActions(
    private val navController: NavController,
) {
    fun navigateBack() {
        navController.popBackStack()
    }

    fun navigateUp() {
        navController.navigateUp()
    }

    fun navigateInsideRssFeed(destination: RssFeedDestination.Feed) {
        Log.d(MAIN_VIEW_TAG, "Navigate inside RSS feed to screen: $destination")
        navController.navigate(destination)
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

    fun navigateToRssFeed() {
        Log.d(MAIN_VIEW_TAG, "Navigate to RSS feed screen")
        navController.navigate(MainDestination.RssFeed) {
            popUpTo(MainDestination.Loading) {
                inclusive = true
            }
            launchSingleTop = true
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

    fun navigateToSomethingWrong() {
        Log.d(MAIN_VIEW_TAG, "Navigate to something went wrong screen")
        navController.navigate(MainDestination.SomethingWentWrong) {
            popUpTo(MainDestination.Loading) {
                inclusive = true
            }
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
