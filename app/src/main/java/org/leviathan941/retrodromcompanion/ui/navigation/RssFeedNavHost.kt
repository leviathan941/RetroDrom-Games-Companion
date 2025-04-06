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

import androidx.compose.material3.DrawerState
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import androidx.navigation.toRoute
import org.leviathan941.retrodromcompanion.notification.RETRODROM_FEED_ITEM_BASE_PATH
import org.leviathan941.retrodromcompanion.ui.promo.RssFeedPromo
import org.leviathan941.retrodromcompanion.ui.screen.RssFeedScreen
import org.leviathan941.retrodromcompanion.ui.screen.RssItemDescriptionScreen
import org.leviathan941.retrodromcompanion.ui.screen.RssLoadingItemScreen
import org.leviathan941.retrodromcompanion.ui.toDestination

fun NavGraphBuilder.rssFeedNavHost(
    navigationActions: MainNavActions,
    drawerState: DrawerState,
) {
    composable<RssFeedDestination.Feed> { backStackEntry ->
        RssFeedScreen(
            backStackEntry = backStackEntry,
            drawerState = drawerState,
            itemClicked = { item ->
                item.toDestination()?.let {
                    navigationActions.navigateToRssItemDescription(it)
                }
            }
        )

        RssFeedPromo(backStackEntry)
    }

    composable<RssFeedDestination.ItemDescription> { backStackEntry ->
        RssItemDescriptionScreen(
            itemDescription = backStackEntry.toRoute(),
            navigationActions = navigationActions
        )
    }

    composable<RssFeedDestination.LoadingItem>(
        deepLinks = listOf(
            navDeepLink<RssFeedDestination.LoadingItem>(
                basePath = RETRODROM_FEED_ITEM_BASE_PATH,
            ),
        ),
    ) { backStackEntry ->
        RssLoadingItemScreen(
            backStackEntry = backStackEntry,
            navActions = navigationActions,
        )
    }
}
