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

import org.leviathan941.retrodromcompanion.ui.screen.loading.LoadingState
import org.leviathan941.retrodromcompanion.ui.topbar.TopBarAction
import org.leviathan941.retrodromcompanion.ui.topbar.TopBarNavButton
import org.leviathan941.retrodromcompanion.ui.topbar.TopBarPrefs

sealed interface MainNavScreen {
    val destination: MainDestination
    val topBarPrefs: TopBarPrefs

    data class Loading(
        val title: String = "",
        val state: LoadingState = LoadingState.InProgress,
    ) : MainNavScreen {
        override val destination: MainDestination = MainDestination.LOADING
        override val topBarPrefs: TopBarPrefs
            get() = TopBarPrefs(
                title,
            )
    }

    data class RssFeed(
        val id: Int,
        val title: String,
        val channelUrl: String,
    ) : MainNavScreen {
        override val destination: MainDestination = MainDestination.RSS_FEED
        override val topBarPrefs: TopBarPrefs
            get() = TopBarPrefs(
                title = title,
                navButton = TopBarNavButton.DRAWER,
            )
    }

    data class WebView(
        val title: String,
        val subtitle: String,
        val url: String,
    ) : MainNavScreen {
        override val destination: MainDestination = MainDestination.WEB_VIEW
        override val topBarPrefs: TopBarPrefs
            get() = TopBarPrefs(
                title = title,
                subtitle = subtitle,
                navButton = TopBarNavButton.CLOSE,
                actions = listOf(TopBarAction.BROWSE),
            )
    }
}
