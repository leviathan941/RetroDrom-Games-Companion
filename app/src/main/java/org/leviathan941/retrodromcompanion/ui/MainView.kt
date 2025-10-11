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

import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import kotlinx.coroutines.launch
import org.leviathan941.retrodromcompanion.R
import org.leviathan941.retrodromcompanion.common.Constants
import org.leviathan941.retrodromcompanion.ui.drawer.DrawerNavigationContent
import org.leviathan941.retrodromcompanion.ui.drawer.DrawerView
import org.leviathan941.retrodromcompanion.ui.model.MainViewModel
import org.leviathan941.retrodromcompanion.ui.model.ViewModelKeys
import org.leviathan941.retrodromcompanion.ui.navigation.MainNavActionsImpl
import org.leviathan941.retrodromcompanion.ui.navigation.MainNavHost
import org.leviathan941.retrodromcompanion.ui.navigation.MainNavPredefinedDestinations
import org.leviathan941.retrodromcompanion.ui.navigation.RssFeedDestination

@Composable
fun MainView(
    navController: NavHostController,
    mainViewModel: MainViewModel = hiltViewModel(
        key = ViewModelKeys.MAIN_VIEW_MODEL,
    ),
) {
    val predefinedDestinations = MainNavPredefinedDestinations(
        rssFeedStart = RssFeedDestination.Feed(
            id = MAIN_RSS_FEED_ID,
            title = stringResource(id = R.string.main_rss_feed_title),
            channelUrl = Constants.RETRODROM_BASE_URL,
        ),
    )
    val navigationActions = remember(navController, predefinedDestinations) {
        MainNavActionsImpl(
            navController,
            predefinedDestinations,
        )
    }
    val navBackStackEntry by navController.currentBackStackEntryAsState()

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()
    val closeDrawer: () -> Unit = { coroutineScope.launch { drawerState.close() } }

    ModalNavigationDrawer(
        drawerContent = {
            DrawerView(
                drawerState = drawerState,
                closeDrawer = closeDrawer,
                onHeaderClick = {
                    navigationActions.navigateToStartedRssFeed()
                },
                navigationContent = {
                    DrawerNavigationContent(
                        uiState = mainViewModel.uiState,
                        navigationActions = navigationActions,
                        navBackStackEntry = navBackStackEntry,
                        drawerState = drawerState,
                        coroutineScope = coroutineScope,
                    )
                },
            )
        },
        drawerState = drawerState,
        gesturesEnabled = drawerState.isOpen,
    ) {
        MainNavHost(
            navHostController = navController,
            navigationActions = navigationActions,
            predefinedDestinations = predefinedDestinations,
            uiState = mainViewModel.uiState,
            drawerState = drawerState,
        )
    }
}
