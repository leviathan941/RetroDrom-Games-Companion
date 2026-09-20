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

package org.leviathan941.retrodromcompanion.network.cache.internal.room.feed.channel

import androidx.paging.PagingSource
import androidx.room3.Dao
import androidx.room3.Insert
import androidx.room3.Query
import org.leviathan941.retrodromcompanion.network.cache.api.feed.FeedChannelItem

@Dao
internal interface RoomFeedChannelItemDao {
    @Query(
        """
        SELECT title, link, pub_date AS pubDate, categories, creator, description,
               post_id AS postId
        FROM feed_channel_items
        WHERE channel_url = :channelUrl
        ORDER BY auto_id ASC
        """,
    )
    fun pagingSource(channelUrl: String): PagingSource<Int, FeedChannelItem>

    @Query(
        """
        SELECT title, link, pub_date AS pubDate, categories, creator, description,
               post_id AS postId
        FROM feed_channel_items
        WHERE channel_url = :channelUrl
          AND post_id = :postId
        LIMIT 1
        """,
    )
    suspend fun findByPostId(
        channelUrl: String,
        postId: String,
    ): FeedChannelItem?

    @Query(
        """
        SELECT MAX(page_number) FROM feed_channel_items
        WHERE channel_url = :channelUrl
        """,
    )
    suspend fun lastPageNumber(channelUrl: String): Int?

    @Insert
    suspend fun addAll(entities: List<RoomFeedChannelItemEntity>)

    @Query(
        """
        DELETE FROM feed_channel_items
        WHERE channel_url = :channelUrl
          AND page_number = :pageNumber
        """,
    )
    suspend fun clearPage(
        channelUrl: String,
        pageNumber: Int,
    )

    @Query(
        """
        DELETE FROM feed_channel_items
        WHERE channel_url = :channelUrl
        """,
    )
    suspend fun clearAll(channelUrl: String)
}
