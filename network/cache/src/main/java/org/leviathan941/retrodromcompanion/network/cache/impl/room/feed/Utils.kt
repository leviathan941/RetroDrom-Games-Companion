/*
 * RetroDrom Games Companion
 * Copyright (C) 2026 Alexey Kuzin <amkuzink@gmail.com>.
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

package org.leviathan941.retrodromcompanion.network.cache.impl.room.feed

import kotlinx.collections.immutable.toImmutableList
import org.leviathan941.retrodromcompanion.network.cache.internal.room.feed.category.RoomFeedCategoryEntity
import org.leviathan941.retrodromcompanion.network.cache.internal.room.feed.channel.RoomFeedChannelItemEntity
import org.leviathan941.retrodromcompanion.network.wordpress.response.WpFeedCategory
import org.leviathan941.retrodromcompanion.network.wordpress.response.WpFeedItem

internal const val FEED_TAG = "RoomFeedCache"

internal fun WpFeedItem.toEntityOrNull(
    channelUrl: String,
    pageNumber: Int,
): RoomFeedChannelItemEntity? {
    val titleValue = title?.takeIf { it.isNotBlank() } ?: return null
    val linkValue = link?.takeIf { it.isNotBlank() } ?: return null
    val pubDateValue = pubDate?.takeIf { it.isNotBlank() } ?: return null
    val descriptionValue = description?.takeIf { it.isNotBlank() } ?: return null
    return RoomFeedChannelItemEntity(
        channelUrl = channelUrl,
        pageNumber = pageNumber,
        title = titleValue,
        link = linkValue,
        pubDate = pubDateValue,
        categories = categories.toImmutableList(),
        creator = creator.orEmpty(),
        description = descriptionValue,
        postId = postId.orEmpty(),
    )
}

internal fun WpFeedCategory.toEntity(): RoomFeedCategoryEntity = RoomFeedCategoryEntity(
    id = id,
    name = name,
    link = link,
    postsCount = postsCount,
)
