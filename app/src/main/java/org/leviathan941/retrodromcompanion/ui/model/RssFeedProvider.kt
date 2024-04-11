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
import org.leviathan941.retrodromcompanion.rssreader.RssChannel
import org.leviathan941.retrodromcompanion.rssreader.RssFetcher
import org.leviathan941.retrodromcompanion.ui.MAIN_VIEW_TAG
import java.io.InvalidClassException

class RssFeedProvider(
    private val channelUrl: String,
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
    suspend fun fetch(useCache: Boolean): RssChannel? {
        return try {
            fetchRssChannel(useCache)
        } catch (e: InvalidClassException) {
            val error = if (useCache) FetchError.INVALID_CACHE else FetchError.UNKNOWN_ERROR
            throw FetchException(error, e)
        } catch (e: Exception) {
            throw FetchException(FetchError.UNKNOWN_ERROR, e)
        }
    }

    private suspend fun fetchRssChannel(useCache: Boolean): RssChannel? {
        return RssFetcher.fetchFeed(
            channelUrl,
            useCache,
        )?.let { rssChannel ->
            Log.d(MAIN_VIEW_TAG, "Received RSS channel: $rssChannel")
            rssChannel
        } ?: run {
            Log.e(MAIN_VIEW_TAG, "Failed to fetch RSS channel for $channelUrl.")
            null
        }
    }
}
