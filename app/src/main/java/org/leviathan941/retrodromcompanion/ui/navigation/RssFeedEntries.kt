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
import androidx.compose.runtime.remember
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import org.leviathan941.retrodromcompanion.ui.promo.RssFeedPromo
import org.leviathan941.retrodromcompanion.ui.screen.RssFeedScreen
import org.leviathan941.retrodromcompanion.ui.screen.RssItemDescriptionScreen
import org.leviathan941.retrodromcompanion.ui.screen.RssLoadingItemScreen
import org.leviathan941.retrodromcompanion.ui.toDestination
import org.leviathan941.retrodromcompanion.ui.toScreen

fun EntryProviderScope<NavKey>.rssFeedEntries(
    navigationActions: MainNavActions,
    drawerState: DrawerState,
) {
    entry<RssFeedDestination.Feed> { destination ->
        val screen = remember(destination) { destination.toScreen() }
        RssFeedScreen(
            screen = screen,
            drawerState = drawerState,
            itemClicked = { item ->
                item.toDestination()?.let {
                    navigationActions.navigateToRssItemDescription(it)
                }
            },
        )

        RssFeedPromo()
    }

    entry<RssFeedDestination.ItemDescription> { destination ->
        RssItemDescriptionScreen(
            itemDescription = destination,
            navigationActions = navigationActions,
        )
    }

    entry<RssFeedDestination.LoadingItem> { destination ->
        RssLoadingItemScreen(
            item = destination,
            navActions = navigationActions,
        )
    }
}
