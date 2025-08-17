/*
 * RetroDrom Games Companion
 * Copyright (C) 2025 Alexey Kuzin <amkuzink@gmail.com>.
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

import org.leviathan941.retrodromcompanion.rssreader.internal.RssFetcher
import org.leviathan941.retrodromcompanion.rssreader.internal.toPublic

class RssItemFinder(
    private val channelUrl: String,
) {
    suspend fun findByPostId(postId: String): RssChannelItem? {
        var pageNumber = 0
        do {
            val items = fetchChannelPageItems(++pageNumber)
            items.find { it.postId == postId }?.let {
                return it
            }
        } while (items.isNotEmpty())
        return null
    }

    private suspend fun fetchChannelPageItems(pageNumber: Int): List<RssChannelItem> =
        RssFetcher.fetchFeedPage(
            channelUrl = channelUrl,
            pageNumber = pageNumber,
            useCache = false,
        ).items?.mapNotNull { it.toPublic() } ?: emptyList()
}
