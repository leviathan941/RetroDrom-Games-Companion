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

data class RssChannelItem(
    val title: String,
    val link: String,
    val pubDate: RssPublicationDate,
    val categories: List<String>,
    val description: RssDescription?,
    val creator: String?,
    val postId: String?,
)

data class RssPublicationDate(
    val value: String,
)

data class RssDescription(
    val imageUrl: String?,
    val paragraphs: List<String>,
    val html: String,
)
