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
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.launch
import org.leviathan941.retrodromcompanion.ui.drawer.DrawerNavigationContent
import org.leviathan941.retrodromcompanion.ui.drawer.DrawerView
import org.leviathan941.retrodromcompanion.ui.model.MainViewModel
import org.leviathan941.retrodromcompanion.ui.model.MainViewModelFactory
import org.leviathan941.retrodromcompanion.ui.navigation.MainDestination
import org.leviathan941.retrodromcompanion.ui.navigation.MainNavActions
import org.leviathan941.retrodromcompanion.ui.navigation.MainNavHost
import org.leviathan941.retrodromcompanion.ui.screen.loading.LoadingState

@Composable
fun MainView(
    activity: ComponentActivity,
) {
    val navController = rememberNavController()
    val navigationActions = remember(navController) {
        MainNavActions(navController)
    }
    val navBackStackEntry by navController.currentBackStackEntryAsState()

    val viewModelStoreOwner = requireNotNull(LocalViewModelStoreOwner.current)
    val mainViewModel: MainViewModel = viewModel(
        viewModelStoreOwner = viewModelStoreOwner,
        factory = MainViewModelFactory(activity.application),
    )

    val uiState by mainViewModel.uiState.collectAsState()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()
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
                    DrawerNavigationContent(
                        uiState = uiState,
                        navigationActions = navigationActions,
                        navBackStackEntry = navBackStackEntry,
                        drawerState = drawerState,
                        coroutineScope = coroutineScope,
                    )
                }
            )
        },
        drawerState = drawerState,
        gesturesEnabled = drawerState.isOpen,
    ) {
        MainNavHost(
            navHostController = navController,
            navigationActions = navigationActions,
            uiState = uiState,
            mainViewModel = mainViewModel,
            drawerState = drawerState,
        )
    }

    LaunchedEffect(key1 = uiState) {
        if (navController.currentDestination?.hasRoute<MainDestination.Loading>() == true &&
            uiState.loadingData.state is LoadingState.Success
        ) {
            Log.d(MAIN_VIEW_TAG, "Navigate after success loading")
            navigationActions.navigateToRssFeed()
        }
    }
}
