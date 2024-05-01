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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalUriHandler
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import kotlinx.coroutines.launch
import org.leviathan941.retrodromcompanion.ui.drawer.DrawerView
import org.leviathan941.retrodromcompanion.ui.drawer.RssFeedDrawerNavView
import org.leviathan941.retrodromcompanion.ui.model.MainViewModel
import org.leviathan941.retrodromcompanion.ui.model.MainViewModelFactory
import org.leviathan941.retrodromcompanion.ui.navigation.MainDestination
import org.leviathan941.retrodromcompanion.ui.navigation.MainNavActions
import org.leviathan941.retrodromcompanion.ui.screen.LoadingScreen
import org.leviathan941.retrodromcompanion.ui.screen.RssFeedScreen
import org.leviathan941.retrodromcompanion.ui.screen.SomethingWrongScreen
import org.leviathan941.retrodromcompanion.ui.screen.loading.LoadingState
import org.leviathan941.retrodromcompanion.ui.topbar.TopBarAction
import org.leviathan941.retrodromcompanion.ui.topbar.TopBarNavButton
import org.leviathan941.retrodromcompanion.ui.topbar.TopBarView
import org.leviathan941.retrodromcompanion.utils.openUrlInCustomTab

@Composable
fun MainView(
    activity: ComponentActivity,
) {
    val navController = rememberNavController()
    val navigationActions = remember(navController) {
        MainNavActions(navController)
    }

    val viewModelStoreOwner = requireNotNull(LocalViewModelStoreOwner.current)
    val mainViewModel: MainViewModel = viewModel(
        viewModelStoreOwner = viewModelStoreOwner,
        factory = MainViewModelFactory(activity.application),
    )

    val uiState by mainViewModel.uiState.collectAsState()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val topBarPrefsState = remember {
        mutableStateOf(uiState.loadingData.topBarPrefs)
    }
    val coroutineScope = rememberCoroutineScope()
    val uriHandler = LocalUriHandler.current
    val clipboardManager = LocalClipboardManager.current
    val closeDrawer: () -> Unit = { coroutineScope.launch { drawerState.close() } }

    ModalNavigationDrawer(
        drawerContent = {
            DrawerView(
                drawerState = drawerState,
                closeDrawer = closeDrawer,
                onHeaderClick = {
                    navigationActions.navigateToRssFeed()
                },
                navigationContent = {
                    uiState.rssFeedData.takeUnless { it.isEmpty() }?.let { rssScreens ->
                        RssFeedDrawerNavView(
                            rssScreens = rssScreens.values.toList(),
                            isSelected = { screen ->
                                navController.currentDestination?.route == "${screen.id}"
                            },
                            onClick = { screen ->
                                closeDrawer()
                                navigationActions.navigateInsideRssFeed(screen)
                            }
                        )
                    }
                }
            )
        },
        drawerState = drawerState,
    ) {
        Scaffold(
            topBar = {
                TopBarView(
                    prefs = topBarPrefsState.value,
                    onNavButtonCLick = { button ->
                        when (button) {
                            TopBarNavButton.BACK -> navigationActions.navigateBack()
                            TopBarNavButton.CLOSE -> navigationActions.navigateUp()
                            TopBarNavButton.DRAWER -> coroutineScope.launch {
                                drawerState.open()
                            }

                            TopBarNavButton.NONE -> Unit
                        }
                    },
                    onActionClick = { action ->
                        when (action) {
                            is TopBarAction.Browse -> {
                                uriHandler.openUri(action.url)
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
                    LoadingScreen(
                        modifier = Modifier.fillMaxSize(),
                        loadingState = uiState.loadingData.state,
                        onErrorLongPress = { message ->
                            clipboardManager.setText(message)
                        },
                        onRetryClick = {
                            mainViewModel.fetchRssData()
                        }
                    )
                    SideEffect {
                        topBarPrefsState.value = uiState.loadingData.topBarPrefs
                    }
                }

                navigation(
                    route = MainDestination.RSS_FEED.route,
                    startDestination = "$MAIN_RSS_FEED_ID",
                ) {
                    uiState.rssFeedData.forEach { (id, screen) ->
                        composable(
                            route = "$id",
                        ) {
                            val toolbarColorInt = MaterialTheme.colorScheme.primaryContainer
                                .toArgb()
                            RssFeedScreen(
                                screen = screen,
                                urlOpener = { url ->
                                    openUrlInCustomTab(
                                        activity,
                                        url,
                                        toolbarColor = toolbarColorInt,
                                    )
                                }
                            )
                            SideEffect {
                                topBarPrefsState.value = screen.topBarPrefs
                            }
                        }
                    }
                }

                composable(MainDestination.SOMETHING_WENT_WRONG.route) {
                    SomethingWrongScreen(
                        onRestartButtonClick = {
                            mainViewModel.fetchRssData()
                            navigationActions.navigateToLoading()
                        }
                    )
                    SideEffect {
                        topBarPrefsState.value = uiState.somethingWrongData.topBarPrefs
                    }
                }
            }
        }
    }

    LaunchedEffect(key1 = uiState) {
        if (navController.currentDestination?.route == MainDestination.LOADING.route &&
            uiState.loadingData.state is LoadingState.Success
        ) {
            Log.d(MAIN_VIEW_TAG, "Navigate after success loading")
            navigationActions.navigateToRssFeed()
        }
    }
}
