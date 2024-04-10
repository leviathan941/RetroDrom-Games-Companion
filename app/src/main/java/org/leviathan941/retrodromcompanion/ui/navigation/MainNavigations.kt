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

import androidx.navigation.NavController

enum class MainDestination(val route: String) {
    LOADING("Loading"),
    RSS_FEED("RssFeed"),
    ;
}

class MainNavActions(
    private val navController: NavController,
) {
    fun navigateTo(screen: MainNavScreen) {
        if (navController.currentDestination?.route == screen.route) return
        navController.navigate(screen.route) {
            popUpTo(MainDestination.LOADING.route)
            launchSingleTop = true
        }
    }
}
