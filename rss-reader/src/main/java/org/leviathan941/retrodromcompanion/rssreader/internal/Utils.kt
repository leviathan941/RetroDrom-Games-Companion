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

import kotlinx.collections.immutable.toImmutableList
import org.leviathan941.retrodromcompanion.network.cache.api.feed.FeedChannelItem
import org.leviathan941.retrodromcompanion.rssreader.RssChannelItem
import org.leviathan941.retrodromcompanion.rssreader.RssDescription
import org.leviathan941.retrodromcompanion.rssreader.RssPublicationDate

internal fun FeedChannelItem.toPublic(): RssChannelItem = RssChannelItem(
    title = title,
    link = link,
    pubDate = RssPublicationDate(pubDate),
    categories = categories,
    description = parseRssDescription(description),
    creator = creator.takeIf { it.isNotBlank() },
    postId = postId.takeIf { it.isNotBlank() },
)

private fun parseRssDescription(description: String): RssDescription =
    HtmlParser(description).parse().let {
        RssDescription(
            imageUrl = it.images.firstOrNull(),
            paragraphs = it.paragraphs.toImmutableList(),
            html = description,
        )
    }
