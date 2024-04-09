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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import tw.ktrssreader.config.readerGlobalConfig
import tw.ktrssreader.generated.RssChannelReader

class RssReader(
    private val baseUrl: String,
) {
    val feed: Flow<RssChannel> by lazy {
        RssChannelReader.flowRead(
            url = Uri.parse(baseUrl).buildUpon()
                .appendPath(FEED_URL_SUFFIX)
                .build()
                .toString()
        ).flowOn(Dispatchers.IO)
    }

    init {
        readerGlobalConfig {
            enableLog = BuildConfig.DEBUG
        }
    }

    private companion object {
        private const val FEED_URL_SUFFIX = "feed"
    }
}
