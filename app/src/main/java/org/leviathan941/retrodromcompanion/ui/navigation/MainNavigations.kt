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

enum class MainDestination(val route: String) {
    LOADING("Loading"),
    RSS_FEED("RssFeed"),
    WEB_VIEW("WebView"),
    SOMETHING_WENT_WRONG("SomethingWentWrong"),
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

    fun navigateInsideRssFeed(screen: MainNavScreen.RssFeed) {
        Log.d(MAIN_VIEW_TAG, "Navigate inside RSS feed to screen: $screen")
        navController.navigate(route = "${screen.id}") {
            launchSingleTop = true
        }
    }

    fun navigateToLoading() {
        Log.d(MAIN_VIEW_TAG, "Navigate to loading screen")
        navController.navigate(MainDestination.LOADING.route) {
            popUpTo(MainDestination.LOADING.route) {
                inclusive = true
            }
            launchSingleTop = true
        }
    }

    fun navigateToRssFeed() {
        Log.d(MAIN_VIEW_TAG, "Navigate to RSS feed screen")
        navController.navigate(MainDestination.RSS_FEED.route) {
            popUpTo(MainDestination.LOADING.route) {
                inclusive = true
            }
            launchSingleTop = true
        }
    }

    fun navigateToWebView() {
        Log.d(MAIN_VIEW_TAG, "Navigate to web view screen")
        navController.navigate(MainDestination.WEB_VIEW.route) {
            launchSingleTop = true
        }
    }

    fun navigateToSomethingWrong() {
        Log.d(MAIN_VIEW_TAG, "Navigate to something went wrong screen")
        navController.navigate(MainDestination.SOMETHING_WENT_WRONG.route) {
            popUpTo(MainDestination.LOADING.route) {
                inclusive = true
            }
            launchSingleTop = true
        }
    }
}
