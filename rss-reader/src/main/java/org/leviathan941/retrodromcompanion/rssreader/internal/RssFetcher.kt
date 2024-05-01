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

package org.leviathan941.retrodromcompanion.rssreader.internal

import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.leviathan941.retrodromcompanion.rssreader.BuildConfig
import tw.ktrssreader.Reader
import tw.ktrssreader.config.readerGlobalConfig
import tw.ktrssreader.generated.ParsedRssChannelReader
import kotlin.time.ExperimentalTime

internal object RssFetcher {
    init {
        readerGlobalConfig {
            enableLog = BuildConfig.DEBUG
        }
    }

    suspend fun fetchFeed(
        channelUrl: String,
        pageNumber: Int,
        useCache: Boolean = true,
        flushCache: Boolean = false,
    ): ParsedRssChannel = withContext(Dispatchers.IO) {
        coRead(channelUrl, pageNumber, useCache, flushCache)
    }

    @OptIn(ExperimentalTime::class)
    private suspend fun coRead(
        channelUrl: String,
        pageNumber: Int,
        useCache: Boolean,
        flushCache: Boolean,
    ): ParsedRssChannel {
        return ParsedRssChannelReader.coRead(
            url = Uri.parse(channelUrl).buildUpon()
                .appendPath(FEED_URL_SUFFIX)
                .appendQueryParameter(PAGE_QUERY_PARAM, pageNumber.toString())
                .build()
                .toString(),
            config = {
                this.useCache = useCache
                this.flushCache = flushCache
                this.expiredTimeMillis = EXPIRED_TIME_MILLIS
            }
        )
    }

    fun clearCache() {
        Reader.clearCache()
    }
}
