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
import org.leviathan941.retrodromcompanion.ui.MAIN_VIEW_TAG
import org.leviathan941.retrodromcompanion.ui.rssFeedScreenRoute

enum class MainDestination(val route: String) {
    LOADING("Loading"),
    RSS_FEED("RssFeed"),
    WEB_VIEW("WebView"),
    ;
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

    fun navigateTo(screen: MainNavScreen) {
        Log.d(MAIN_VIEW_TAG, "Navigate to: $screen")
        when (screen) {
            is MainNavScreen.Loading -> navigateToLoading()
            is MainNavScreen.RssFeed -> navigateToRssFeed(screen.id)
            is MainNavScreen.WebView -> navigateToWebView()
        }
    }

    private fun navigateToLoading() {
        navController.navigate(MainDestination.LOADING.route) {
            popUpTo(MainDestination.LOADING.route) {
                inclusive = true
            }
            launchSingleTop = true
        }
    }

    private fun navigateToRssFeed(channelId: Int) {
        navController.navigate(rssFeedScreenRoute(channelId)) {
            popUpTo(MainDestination.LOADING.route) {
                inclusive = true
            }
            launchSingleTop = true
        }
    }

    private fun navigateToWebView() {
        navController.navigate(MainDestination.WEB_VIEW.route) {
            launchSingleTop = true
        }
    }
}
