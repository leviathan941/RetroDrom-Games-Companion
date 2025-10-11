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

package org.leviathan941.retrodromcompanion.ui.navigation

import android.util.Log
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.toRoute
import org.leviathan941.retrodromcompanion.ui.MAIN_VIEW_TAG

data class MainNavPredefinedDestinations(
    val rssFeedStart: RssFeedDestination.Feed,
)

interface MainNavActions {
    fun navigateBack()
    fun navigateToStartedRssFeed()
    fun navigateToRssFeed(destination: RssFeedDestination.Feed)
    fun navigateToRssItemDescription(destination: RssFeedDestination.ItemDescription)
    fun navigateToSettings()
    fun navigateToSettingsItem(destination: SettingsDestination)
}

class MainNavActionsImpl(
    private val navController: NavController,
    private val predefinedDestinations: MainNavPredefinedDestinations,
) : MainNavActions {
    override fun navigateBack() {
        if (navController.popBackStack()) {
            Log.d(MAIN_VIEW_TAG, "Navigate back")
        } else {
            Log.d(MAIN_VIEW_TAG, "No back stack entry, cannot navigate back")
            navigateToStartedRssFeed()
        }
    }

    override fun navigateToStartedRssFeed() {
        navigateToRssFeed(predefinedDestinations.rssFeedStart)
    }

    override fun navigateToRssFeed(destination: RssFeedDestination.Feed) {
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
            popUpTo<RssFeedDestination.Feed> {
                inclusive = isInsideRssFeed
            }
            launchSingleTop = true
        }
    }

    override fun navigateToRssItemDescription(destination: RssFeedDestination.ItemDescription) {
        Log.d(MAIN_VIEW_TAG, "Navigate to RSS item description screen")
        navController.navigate(destination) {
            popUpTo<RssFeedDestination.Feed>()
            launchSingleTop = true
        }
    }

    override fun navigateToSettings() {
        Log.d(MAIN_VIEW_TAG, "Navigate to settings screen")
        navController.navigate(MainDestination.Settings) {
            launchSingleTop = true
        }
    }

    override fun navigateToSettingsItem(destination: SettingsDestination) {
        Log.d(MAIN_VIEW_TAG, "Navigate to $destination settings screen")
        navController.navigate(destination) {
            launchSingleTop = true
        }
    }
}

class MainNavActionsStub : MainNavActions {
    override fun navigateBack() = Unit
    override fun navigateToStartedRssFeed() = Unit
    override fun navigateToRssFeed(destination: RssFeedDestination.Feed) = Unit
    override fun navigateToRssItemDescription(destination: RssFeedDestination.ItemDescription) =
        Unit
    override fun navigateToSettings() = Unit
    override fun navigateToSettingsItem(destination: SettingsDestination) = Unit
}
