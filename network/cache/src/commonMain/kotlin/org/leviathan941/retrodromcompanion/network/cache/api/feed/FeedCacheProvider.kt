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

package org.leviathan941.retrodromcompanion.network.cache.api.feed

import androidx.paging.PagingSource
import kotlinx.coroutines.flow.Flow

public interface FeedCacheProvider {
    public val categories: Flow<List<FeedCategory>>

    /**
     * Items come back in the order they were fetched, and the source is invalidated whenever
     * [FeedCacheMutator] changes the cache.
     */
    public fun channelItemsPagingSource(channelUrl: String): PagingSource<Int, FeedChannelItem>

    public suspend fun findChannelItemByPostId(
        channelUrl: String,
        postId: String,
    ): FeedChannelItem?

    public suspend fun channelItemsLastUpdatedMillis(channelUrl: String): Long?

    public suspend fun channelItemsLastPageNumber(channelUrl: String): Int?
}
