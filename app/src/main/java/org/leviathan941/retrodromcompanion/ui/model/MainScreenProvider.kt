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

package org.leviathan941.retrodromcompanion.ui.model

import android.util.Log
import org.leviathan941.retrodromcompanion.rssreader.RssReader
import org.leviathan941.retrodromcompanion.ui.MAIN_VIEW_TAG
import org.leviathan941.retrodromcompanion.ui.navigation.MainNavScreen

class MainScreenProvider(
    baseUrl: String,
) {
    private val rssReader = RssReader(baseUrl)

    val initialScreens = listOf(
        MainNavScreen.Loading,
    )

    suspend fun fetchScreens(): List<MainNavScreen> {
        return initialScreens +
                safeFetchRssChannelScreens()
    }

    private suspend fun safeFetchRssChannelScreens(
        useCache: Boolean = true,
    ): List<MainNavScreen> {
        return try {
            fetchRssChannelScreens(useCache)
        } catch (e: Exception) {
            Log.e(MAIN_VIEW_TAG, "Failed to collect home feed. Clear cache and try again.", e)
            fetchRssChannelScreens(useCache = false)
        }
    }

    private suspend fun fetchRssChannelScreens(
        useCache: Boolean = true,
    ): List<MainNavScreen> {
        return rssReader.fetchFeedChannels(useCache).let { rssChannels ->
            Log.d(MAIN_VIEW_TAG, "Received RSS channel: $rssChannels")
            rssChannels.map { MainNavScreen.RssFeed(it) }
        }
    }
}
