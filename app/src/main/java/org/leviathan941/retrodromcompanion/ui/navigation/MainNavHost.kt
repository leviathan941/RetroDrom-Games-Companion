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

import androidx.compose.material3.DrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.navigation
import kotlinx.coroutines.flow.StateFlow
import org.leviathan941.retrodromcompanion.ui.model.MainViewState

@Composable
fun MainNavHost(
    navHostController: NavHostController,
    navigationActions: MainNavActions,
    predefinedDestinations: MainNavPredefinedDestinations,
    uiState: StateFlow<MainViewState>,
    drawerState: DrawerState,
    modifier: Modifier = Modifier,
) {
    val uiState by uiState.collectAsState()

    NavHost(
        navController = navHostController,
        startDestination = uiState.destination,
        modifier = modifier,
    ) {

        navigation<MainDestination.RssFeed>(
            startDestination = predefinedDestinations.rssFeedStart,
        ) {
            rssFeedNavHost(
                navigationActions = navigationActions,
                drawerState = drawerState,
            )
        }

        navigation<MainDestination.Settings>(
            startDestination = SettingsDestination.Main,
        ) {
            settingsNavHost(
                navigationActions = navigationActions,
            )
        }
    }
}
