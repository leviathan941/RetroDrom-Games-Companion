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

import org.leviathan941.retrodromcompanion.ui.model.TopBarPrefs

sealed interface MainNavScreen {
    val destination: MainDestination
    val topBarPrefs: TopBarPrefs

    data object Loading : MainNavScreen {
        override val destination: MainDestination = MainDestination.LOADING
        override val topBarPrefs: TopBarPrefs
            get() = TopBarPrefs(
                title = MainDestination.LOADING.route,
            )
    }

    data class RssFeed(
        val title: String,
        val channelUrl: String,
    ) : MainNavScreen {
        override val destination: MainDestination = MainDestination.RSS_FEED
        override val topBarPrefs: TopBarPrefs
            get() = TopBarPrefs(title)
    }

    data class WebView(
        val title: String,
        val url: String,
    ) : MainNavScreen {
        override val destination: MainDestination = MainDestination.WEB_VIEW
        override val topBarPrefs: TopBarPrefs
            get() = TopBarPrefs(title)
    }
}
