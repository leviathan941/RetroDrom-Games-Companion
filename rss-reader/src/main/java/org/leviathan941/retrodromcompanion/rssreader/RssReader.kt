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

package org.leviathan941.retrodromcompanion.rssreader

import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.leviathan941.retrodromcompanion.rssreader.internal.toPublic
import tw.ktrssreader.Reader
import tw.ktrssreader.config.readerGlobalConfig
import tw.ktrssreader.generated.ParsedRssChannelReader

class RssReader(
    private val baseUrl: String,
) {
    init {
        readerGlobalConfig {
            enableLog = BuildConfig.DEBUG
        }
    }

    suspend fun fetchFeedChannels(useCache: Boolean = true): List<RssChannel> =
        withContext(Dispatchers.IO) {
            listOfNotNull(
                coRead(FEED_URL_SUFFIX, useCache),
            )
        }

    fun clearCache() {
        Reader.clearCache()
    }

    private suspend fun coRead(path: String, useCache: Boolean): RssChannel? {
        return ParsedRssChannelReader.coRead(
            url = Uri.parse(baseUrl).buildUpon()
                .appendPath(path)
                .build()
                .toString(),
            config = {
                this.useCache = useCache
                flushCache = !useCache
            }
        ).toPublic()
    }
}
