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

package org.leviathan941.retrodromcompanion.ui.drawer

import androidx.compose.material3.DrawerState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.navigation.NavBackStackEntry
import androidx.navigation.toRoute
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.leviathan941.retrodromcompanion.ui.isRouteActive
import org.leviathan941.retrodromcompanion.ui.model.MainViewState
import org.leviathan941.retrodromcompanion.ui.navigation.MainDestination
import org.leviathan941.retrodromcompanion.ui.navigation.MainNavActions
import org.leviathan941.retrodromcompanion.ui.navigation.RssFeedDestination
import org.leviathan941.retrodromcompanion.ui.toDestination

@Composable
fun DrawerNavigationContent(
    uiState: MainViewState,
    navigationActions: MainNavActions,
    navBackStackEntry: NavBackStackEntry?,
    drawerState: DrawerState,
    coroutineScope: CoroutineScope,
) {
    val closeDrawer: () -> Unit = { coroutineScope.launch { drawerState.close() } }

    SettingsDrawerNavView(
        isSelected = {
            navBackStackEntry?.isRouteActive<MainDestination.Settings>() == true
        },
        onClick = {
            navigationActions.navigateToSettings()
            closeDrawer()
        },
    )

    HorizontalDivider()

    uiState.rssFeedData.takeUnless { it.isEmpty() }?.let { rssScreens ->
        RssFeedDrawerNavView(
            rssScreens = rssScreens.values.toList(),
            isSelected = { screen ->
                navBackStackEntry?.run {
                    isRouteActive<RssFeedDestination.Feed>() &&
                        toRoute<RssFeedDestination.Feed>() == screen.toDestination()
                } == true
            },
            onClick = { screen ->
                navigationActions.navigateToRssFeed(screen.toDestination())
                closeDrawer()
            },
        )
    }

    HorizontalDivider()
}
