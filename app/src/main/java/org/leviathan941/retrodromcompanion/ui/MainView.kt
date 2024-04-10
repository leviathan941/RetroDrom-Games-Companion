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

package org.leviathan941.retrodromcompanion.ui

import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import org.leviathan941.retrodromcompanion.ui.model.MainViewModel
import org.leviathan941.retrodromcompanion.ui.model.MainViewState
import org.leviathan941.retrodromcompanion.ui.navigation.MainDestination
import org.leviathan941.retrodromcompanion.ui.navigation.MainNavActions
import org.leviathan941.retrodromcompanion.ui.navigation.MainNavScreen
import org.leviathan941.retrodromcompanion.ui.screen.LoadingScreen
import org.leviathan941.retrodromcompanion.ui.screen.RssFeedScreen

@Composable
fun MainView(
    activity: ComponentActivity,
) {
    val mainViewModel: MainViewModel by activity.viewModels()

    val uiState: MainViewState by mainViewModel.uiState.collectAsState()

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    val navController = rememberNavController()
    val navigationActions = remember(navController) {
        MainNavActions(navController)
    }

    ModalNavigationDrawer(
        drawerContent = {
            DrawerView()
        },
        drawerState = drawerState,
    ) {
        Scaffold(
            topBar = {
                TopBarView(
                    prefs = uiState.currentScreen.topBarPrefs,
                )
            }
        ) { paddings ->
            NavHost(
                modifier = Modifier.padding(paddings),
                navController = navController,
                startDestination = MainDestination.LOADING.route,
            ) {
                MainDestination.entries.forEach { destination ->
                    composable(destination.route) {
                        val currentScreen = uiState.currentScreen
                        when (destination) {
                            MainDestination.LOADING -> LoadingScreen()
                            MainDestination.RSS_FEED -> {
                                require(currentScreen is MainNavScreen.RssFeed)
                                RssFeedScreen(currentScreen)
                            }
                        }
                    }
                }
            }
            LaunchedEffect(key1 = uiState) {
                uiState.currentScreen.takeUnless {
                    it.route == navController.currentDestination?.route
                }?.let { screen ->
                    Log.d(MAIN_VIEW_TAG, "Navigate to ${screen.route}")
                    navigationActions.navigateTo(screen)
                }
            }
        }
    }
}
