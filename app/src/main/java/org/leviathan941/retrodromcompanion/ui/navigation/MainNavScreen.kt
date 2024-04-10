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

import org.leviathan941.retrodromcompanion.rssreader.RssChannel
import org.leviathan941.retrodromcompanion.ui.model.TopBarPrefs

sealed interface MainNavScreen {
    val route: String
    val topBarPrefs: TopBarPrefs

    data object Loading : MainNavScreen {
        override val route: String = MainDestination.LOADING.route
        override val topBarPrefs: TopBarPrefs
            get() = TopBarPrefs(
                title = route,
            )
    }

    data class RssFeed(
        val rssChannel: RssChannel,
    ) : MainNavScreen {
        override val route: String = MainDestination.RSS_FEED.route
        override val topBarPrefs: TopBarPrefs
            get() = TopBarPrefs(rssChannel.title)
    }
}
