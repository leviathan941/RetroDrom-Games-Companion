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

import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.material3.DrawerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import org.leviathan941.retrodromcompanion.ui.NAVIGATION_ANIMATION_DURATION_MS

@Composable
fun MainNavHost(
    backStack: NavBackStack<NavKey>,
    navigationActions: MainNavActions,
    drawerState: DrawerState,
    modifier: Modifier = Modifier,
) {
    val popTransition = slideInHorizontally(
        initialOffsetX = { -it },
        animationSpec = tween(durationMillis = NAVIGATION_ANIMATION_DURATION_MS),
    ) togetherWith slideOutHorizontally(
        targetOffsetX = { it },
        animationSpec = tween(durationMillis = NAVIGATION_ANIMATION_DURATION_MS),
    )

    NavDisplay(
        backStack = backStack,
        modifier = modifier,
        onBack = navigationActions::navigateBack,
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            // Scopes the view models an entry obtains to that entry, as Nav2's back stack entry did.
            rememberViewModelStoreNavEntryDecorator(),
        ),
        transitionSpec = {
            slideInHorizontally(
                initialOffsetX = { it },
                animationSpec = tween(durationMillis = NAVIGATION_ANIMATION_DURATION_MS),
            ) togetherWith slideOutHorizontally(
                targetOffsetX = { -it },
                animationSpec = tween(durationMillis = NAVIGATION_ANIMATION_DURATION_MS),
            )
        },
        popTransitionSpec = { popTransition },
        predictivePopTransitionSpec = { popTransition },
        entryProvider = entryProvider {
            rssFeedEntries(
                navigationActions = navigationActions,
                drawerState = drawerState,
            )
            settingsEntries(
                navigationActions = navigationActions,
            )
        },
    )
}
