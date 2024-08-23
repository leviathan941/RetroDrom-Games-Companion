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

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.DrawerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import org.leviathan941.retrodromcompanion.R
import org.leviathan941.retrodromcompanion.ui.BASE_URL
import org.leviathan941.retrodromcompanion.ui.MAIN_RSS_FEED_ID
import org.leviathan941.retrodromcompanion.ui.model.MainViewModel
import org.leviathan941.retrodromcompanion.ui.model.MainViewState
import org.leviathan941.retrodromcompanion.ui.screen.LoadingScreen
import org.leviathan941.retrodromcompanion.ui.screen.SomethingWrongScreen

@Composable
fun MainNavHost(
    navHostController: NavHostController,
    navigationActions: MainNavActions,
    uiState: MainViewState,
    mainViewModel: MainViewModel,
    drawerState: DrawerState,
) {
    val clipboardManager = LocalClipboardManager.current
    val mainRssFeed = RssFeedDestination.Feed(
        id = MAIN_RSS_FEED_ID,
        title = stringResource(id = R.string.main_rss_feed_title),
        channelUrl = BASE_URL,
    )

    NavHost(
        navController = navHostController,
        startDestination = MainDestination.Loading
    ) {
        composable<MainDestination.Loading> {
            LoadingScreen(
                loadingData = uiState.loadingData,
                modifier = Modifier.fillMaxSize(),
                onErrorLongPress = { message ->
                    clipboardManager.setText(message)
                },
                onRetryClick = {
                    mainViewModel.fetchRssData()
                }
            )
        }

        navigation<MainDestination.RssFeed>(
            startDestination = mainRssFeed,
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
                    mainViewModel.fetchRssData()
                    navigationActions.navigateToLoading()
                }
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
