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
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import kotlinx.coroutines.launch
import org.leviathan941.retrodromcompanion.ui.model.MainViewModel
import org.leviathan941.retrodromcompanion.ui.model.MainViewModelFactory
import org.leviathan941.retrodromcompanion.ui.navigation.MainDestination
import org.leviathan941.retrodromcompanion.ui.navigation.MainNavActions
import org.leviathan941.retrodromcompanion.ui.screen.LoadingScreen
import org.leviathan941.retrodromcompanion.ui.screen.RssFeedScreen
import org.leviathan941.retrodromcompanion.ui.screen.WebViewScreen
import org.leviathan941.retrodromcompanion.ui.screen.loading.LoadingState
import org.leviathan941.retrodromcompanion.ui.topbar.MainNavButtonHandler
import org.leviathan941.retrodromcompanion.ui.topbar.TopBarAction
import org.leviathan941.retrodromcompanion.ui.topbar.TopBarView

@Composable
fun MainView(
    activity: ComponentActivity,
) {
    val navController = rememberNavController()
    val navigationActions = remember(navController) {
        MainNavActions(navController)
    }

    val mainViewModel: MainViewModel = viewModel(
        factory = MainViewModelFactory(activity.application),
    )

    val uiState by mainViewModel.uiState.collectAsState()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val topBarPrefsState = remember {
        mutableStateOf(uiState.loadingData.topBarPrefs)
    }
    val coroutineScope = rememberCoroutineScope()
    val uriHandler = LocalUriHandler.current
    val currentNavBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentNavBackStackEntry?.destination?.route ?: MainDestination.LOADING.route

    ModalNavigationDrawer(
        drawerContent = {
            DrawerView(
                currentRoute = currentRoute,
                navActions = navigationActions,
                closeDrawer = {
                    coroutineScope.launch { drawerState.close() }
                },
                rssScreens = uiState.rssFeedData.values.toList(),
            )
        },
        drawerState = drawerState,
    ) {
        Scaffold(
            topBar = {
                TopBarView(
                    prefs = topBarPrefsState.value,
                    navButtonListener = MainNavButtonHandler(
                        navigationActions = navigationActions,
                        drawerState = drawerState,
                        coroutineScope = coroutineScope,
                    ),
                    onActionClick = { action ->
                        when (action) {
                            TopBarAction.BROWSE -> {
                                navController.currentDestinationUrl(uiState)?.let {
                                    uriHandler.openUri(it)
                                }
                            }
                        }
                    }
                )
            }
        ) { paddings ->
            NavHost(
                modifier = Modifier.padding(paddings),
                navController = navController,
                startDestination = MainDestination.LOADING.route,
            ) {
                composable(MainDestination.LOADING.route) {
                    LoadingScreen(uiState.loadingData.state)
                    SideEffect {
                        topBarPrefsState.value = uiState.loadingData.topBarPrefs
                    }
                }

                composable(
                    route = "${MainDestination.RSS_FEED.route}/{$RSS_FEED_NAV_CHANNEL_ID}",
                    arguments = listOf(
                        navArgument(RSS_FEED_NAV_CHANNEL_ID) { type = NavType.IntType },
                    ),
                ) { stackEntry ->
                    val screen = stackEntry.arguments?.getInt(RSS_FEED_NAV_CHANNEL_ID, -1)?.let {
                        uiState.rssFeedData[it]
                    } ?: run {
                        // TODO: Navigate to something goes wrong screen
                        return@composable
                    }
                    RssFeedScreen(
                        screen = screen,
                        webViewOpener = {
                            mainViewModel.setWebViewData(it)
                            navigationActions.navigateTo(it)
                        }
                    )
                    SideEffect {
                        topBarPrefsState.value = screen.topBarPrefs
                    }
                }

                composable(MainDestination.WEB_VIEW.route) {
                    val screen = uiState.webViewData ?: run {
                        // TODO: Navigate to something goes wrong screen
                        return@composable
                    }
                    WebViewScreen(url = screen.url)
                    SideEffect {
                        topBarPrefsState.value = screen.topBarPrefs
                    }
                }
            }
        }
    }

    LaunchedEffect(key1 = uiState) {
        if (navController.currentDestination?.route == MainDestination.LOADING.route &&
            uiState.loadingData.state is LoadingState.Success
        ) {
            uiState.rssFeedData.run {
                getOrElse(MAIN_RSS_FEED_ID) { values.first() }
            }.let {
                Log.d(MAIN_VIEW_TAG, "Navigate after success loading: $it")
                navigationActions.navigateTo(it)
            }
        }
    }
}
