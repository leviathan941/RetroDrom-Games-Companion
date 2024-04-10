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

import org.leviathan941.retrodromcompanion.rssreader.Rss
import tw.ktrssreader.annotation.RssRawData
import tw.ktrssreader.annotation.RssTag
import java.io.Serializable

@RssTag(name = Rss.CHANNEL_TAG)
data class ParsedRssChannel(
    val title: String?,
    val link: String?,
    @RssTag(name = Rss.ITEM_TAG)
    val items: List<ParsedRssChannelItem>?,
) : Serializable

@RssTag(name = Rss.ITEM_TAG)
data class ParsedRssChannelItem(
    val title: String?,
    val link: String?,
    @RssTag(name = Rss.PUBLICATION_DATE_TAG)
    val pubDate: String?,
    @RssTag(name = Rss.CATEGORY_TAG)
    val categories: List<String>?,
    @RssRawData(rawTags = [Rss.DC_CREATOR_TAG])
    val creator: String?,
) : Serializable
