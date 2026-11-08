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

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource
import androidx.navigation3.runtime.rememberNavBackStack
import dev.zacsweers.metrox.viewmodel.metroViewModel
import kotlinx.coroutines.launch
import org.leviathan941.retrodromcompanion.resources.Res
import org.leviathan941.retrodromcompanion.resources.main_rss_feed_title
import org.leviathan941.retrodromcompanion.app.message.UserMessage
import org.leviathan941.retrodromcompanion.common.Constants
import org.leviathan941.retrodromcompanion.ui.drawer.DrawerNavigationContent
import org.leviathan941.retrodromcompanion.ui.drawer.DrawerView
import org.leviathan941.retrodromcompanion.ui.model.MainViewModel
import org.leviathan941.retrodromcompanion.ui.model.UserMessagesViewModel
import org.leviathan941.retrodromcompanion.ui.model.ViewModelKeys
import org.leviathan941.retrodromcompanion.ui.navigation.AppDestination
import org.leviathan941.retrodromcompanion.ui.navigation.MainNavActionsImpl
import org.leviathan941.retrodromcompanion.ui.navigation.MainNavHost
import org.leviathan941.retrodromcompanion.ui.navigation.MainNavPredefinedDestinations
import org.leviathan941.retrodromcompanion.ui.navigation.RssFeedDestination
import org.leviathan941.retrodromcompanion.ui.navigation.appDestinationSavedStateConfiguration

@Composable
fun MainView(
    deepLinkDestination: RssFeedDestination.LoadingItem?,
    modifier: Modifier = Modifier,
    mainViewModel: MainViewModel = metroViewModel(
        key = ViewModelKeys.MAIN_VIEW_MODEL,
    ),
    userMessagesViewModel: UserMessagesViewModel = metroViewModel(
        key = ViewModelKeys.USER_MESSAGES_VIEW_MODEL,
    ),
) {
    val predefinedDestinations = MainNavPredefinedDestinations(
        rssFeedStart = RssFeedDestination.Feed(
            id = MAIN_RSS_FEED_ID,
            title = stringResource(Res.string.main_rss_feed_title),
            channelUrl = Constants.RETRODROM_BASE_URL,
        ),
    )

    // The deep link opens on top of the start feed. Only a new back stack takes these: a restored
    // one keeps what it saved.
    @Suppress("SpreadOperator")
    val backStack = rememberNavBackStack(
        appDestinationSavedStateConfiguration,
        *listOfNotNull(predefinedDestinations.rssFeedStart, deepLinkDestination).toTypedArray(),
    )
    val navigationActions = remember(backStack, predefinedDestinations) {
        MainNavActionsImpl(
            backStack,
            predefinedDestinations,
        )
    }

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()
    val closeDrawer: () -> Unit = { coroutineScope.launch { drawerState.close() } }

    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(userMessagesViewModel) {
        userMessagesViewModel.messages.collect { message ->
            // getString takes its format arguments as a vararg.
            @Suppress("SpreadOperator")
            val text = when (message) {
                is UserMessage.Text -> message.text

                is UserMessage.Resource ->
                    getString(message.textRes, *message.formatArgs.toTypedArray())
            }
            snackbarHostState.showSnackbar(text)
        }
    }

    Box(modifier = modifier) {
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
                            currentDestination = backStack.lastOrNull() as? AppDestination,
                            drawerState = drawerState,
                            coroutineScope = coroutineScope,
                            showMessage = userMessagesViewModel::show,
                        )
                    },
                )
            },
            drawerState = drawerState,
            gesturesEnabled = drawerState.isOpen,
        ) {
            MainNavHost(
                backStack = backStack,
                navigationActions = navigationActions,
                drawerState = drawerState,
            )
        }

        // The one place user messages appear: above the drawer and every screen's own Scaffold.
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .windowInsetsPadding(WindowInsets.safeDrawing),
        )
    }
}
