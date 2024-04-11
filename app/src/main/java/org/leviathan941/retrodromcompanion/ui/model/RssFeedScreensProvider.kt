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
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import org.leviathan941.retrodromcompanion.rssreader.RssFetcher
import org.leviathan941.retrodromcompanion.ui.MAIN_VIEW_TAG
import org.leviathan941.retrodromcompanion.ui.navigation.MainNavScreen
import java.io.InvalidClassException

class RssFeedScreensProvider(
    private val baseUrl: String,
) {
    enum class FetchError {
        UNKNOWN_ERROR,
        INVALID_CACHE,
        ;
    }

    class FetchException(
        val fetchError: FetchError,
        throwable: Throwable,
    ) : Exception(throwable)

    @Throws(FetchException::class)
    suspend fun fetchAllScreens(useCache: Boolean): List<MainNavScreen.RssFeed> {
        val channelUrls = listOf(baseUrl)
        return coroutineScope {
            channelUrls.map {
                async { safeFetchRssScreen(it, useCache) }
            }.awaitAll().filterNotNull()
        }
    }

    @Throws(FetchException::class)
    suspend fun refreshScreen(screen: MainNavScreen.RssFeed): MainNavScreen.RssFeed {
        return safeFetchRssScreen(
            channelUrl = screen.rssChannel.link,
            useCache = false,
        ) ?: screen
    }

    @Throws(FetchException::class)
    private suspend fun safeFetchRssScreen(
        channelUrl: String,
        useCache: Boolean,
    ): MainNavScreen.RssFeed? {
        return try {
            fetchRssScreen(channelUrl, useCache)
        } catch (e: InvalidClassException) {
            val error = if (useCache) FetchError.INVALID_CACHE else FetchError.UNKNOWN_ERROR
            throw FetchException(error, e)
        } catch (e: Exception) {
            throw FetchException(FetchError.UNKNOWN_ERROR, e)
        }
    }

    private suspend fun fetchRssScreen(
        channelUrl: String,
        useCache: Boolean = true,
    ): MainNavScreen.RssFeed? {
        return RssFetcher.fetchFeed(
            channelUrl,
            useCache,
        )?.let { rssChannel ->
            Log.d(MAIN_VIEW_TAG, "Received RSS channel: $rssChannel")
            MainNavScreen.RssFeed(rssChannel)
        } ?: run {
            Log.e(MAIN_VIEW_TAG, "Failed to fetch RSS channel for $channelUrl.")
            null
        }
    }
}
