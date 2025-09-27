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

import android.content.ClipData
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.DrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.LocalClipboard
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import kotlinx.coroutines.launch
import org.leviathan941.retrodromcompanion.ui.model.MainViewState
import org.leviathan941.retrodromcompanion.ui.screen.LoadingScreen
import org.leviathan941.retrodromcompanion.ui.screen.SomethingWrongScreen

@Composable
fun MainNavHost(
    navHostController: NavHostController,
    navigationActions: MainNavActions,
    predefinedDestinations: MainNavPredefinedDestinations,
    uiState: MainViewState,
    drawerState: DrawerState,
    modifier: Modifier = Modifier,
    fetchRssData: () -> Unit,
) {
    val clipboard = LocalClipboard.current
    val coroutineScope = rememberCoroutineScope()

    NavHost(
        navController = navHostController,
        startDestination = MainDestination.Loading,
        modifier = modifier,
    ) {
        composable<MainDestination.Loading> {
            LaunchedEffect(key1 = fetchRssData) {
                fetchRssData()
            }
            LoadingScreen(
                loadingData = uiState.loadingData,
                modifier = Modifier.fillMaxSize(),
                onErrorLongPress = { label, message ->
                    coroutineScope.launch {
                        clipboard.setClipEntry(
                            ClipEntry(
                                ClipData.newPlainText(
                                    label,
                                    message,
                                ),
                            ),
                        )
                    }
                },
                onRetryClick = {
                    fetchRssData()
                },
            )
        }

        navigation<MainDestination.RssFeed>(
            startDestination = predefinedDestinations.rssFeedStart,
        ) {
            rssFeedNavHost(
                navigationActions = navigationActions,
                drawerState = drawerState,
            )
        }

        composable<MainDestination.SomethingWentWrong> {
            SomethingWrongScreen(
                data = uiState.somethingWrongData,
                onRestartButtonClick = {
                    navigationActions.navigateToLoading()
                },
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
