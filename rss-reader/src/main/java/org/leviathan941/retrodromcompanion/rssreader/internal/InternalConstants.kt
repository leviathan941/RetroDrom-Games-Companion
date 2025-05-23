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

import java.util.concurrent.TimeUnit

internal const val FEED_URL_SUFFIX = "feed"
internal const val PAGE_QUERY_PARAM = "paged"
internal val EXPIRED_TIME_MILLIS = TimeUnit.HOURS.toMillis(1L)
internal const val RSS_READER_TAG = "RssReader"

internal object Rss {
    const val CHANNEL_TAG = "channel"
    const val ITEM_TAG = "item"
    const val DC_CREATOR_TAG = "dc:creator"
    const val CATEGORY_TAG = "category"
    const val PUBLICATION_DATE_TAG = "pubDate"
    const val POST_ID_TAG = "post_id"
}
