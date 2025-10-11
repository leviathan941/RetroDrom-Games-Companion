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

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavBackStackEntry
import androidx.navigation.toRoute
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.leviathan941.retrodromcompanion.R
import org.leviathan941.retrodromcompanion.ui.isRouteActive
import org.leviathan941.retrodromcompanion.ui.model.MainViewState
import org.leviathan941.retrodromcompanion.ui.navigation.MainDestination
import org.leviathan941.retrodromcompanion.ui.navigation.MainNavActions
import org.leviathan941.retrodromcompanion.ui.navigation.MainNavActionsStub
import org.leviathan941.retrodromcompanion.ui.navigation.MainNavScreen
import org.leviathan941.retrodromcompanion.ui.navigation.RssFeedDestination
import org.leviathan941.retrodromcompanion.ui.openUrlByIntent
import org.leviathan941.retrodromcompanion.ui.toDestination

@Composable
fun DrawerNavigationContent(
    uiState: StateFlow<MainViewState>,
    navigationActions: MainNavActions,
    navBackStackEntry: NavBackStackEntry?,
    drawerState: DrawerState,
    coroutineScope: CoroutineScope,
) {
    val closeDrawer: () -> Unit = { coroutineScope.launch { drawerState.close() } }
    val uiState by uiState.collectAsState()
    val context = LocalContext.current

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

    when (val state = uiState) {
        is MainViewState.RssFeed -> {
            state.screenData.takeUnless { it.isEmpty() }?.let { rssScreens ->
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
        }
    }

    HorizontalDivider()

    SocialNetworkDrawerNavView(
        title = stringResource(R.string.drawer_telegram_item_title),
        iconUrl = "https://telegram.org/img/t_logo.png",
        badgeContentDescription = stringResource(
            R.string.drawer_telegram_external_link_badge_description,
        ),
        onClick = {
            openUrlByIntent(
                context = context,
                url = "https://t.me/retrodrom",
            )
            closeDrawer()
        },
    )

    HorizontalDivider()
}

@Preview(
    showBackground = true,
)
@Suppress("MagicNumber")
@Composable
private fun DrawerNavigationContentPreview() {
    Column {
        DrawerNavigationContent(
            uiState = MutableStateFlow(
                MainViewState.RssFeed(
                    screenData = mapOf(
                        1 to MainNavScreen.RssFeed(
                            id = 1,
                            title = "Feed 1",
                            channelUrl = "https://example.com/feed1.xml",
                        ),
                        2 to MainNavScreen.RssFeed(
                            id = 2,
                            title = "Feed 2",
                            channelUrl = "https://example.com/feed2.xml",
                        ),
                        3 to MainNavScreen.RssFeed(
                            id = 3,
                            title = "Feed 3",
                            channelUrl = "https://example.com/feed3.xml",
                        ),
                    ),
                ),
            ),
            navigationActions = MainNavActionsStub(),
            navBackStackEntry = null,
            drawerState = rememberDrawerState(initialValue = DrawerValue.Open),
            coroutineScope = rememberCoroutineScope(),
        )
    }
}
