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

package org.leviathan941.retrodromcompanion.network.cache.impl.room.feed

import androidx.paging.PagingSource
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import org.leviathan941.retrodromcompanion.network.cache.api.feed.FeedCacheProvider
import org.leviathan941.retrodromcompanion.network.cache.api.feed.FeedCategory
import org.leviathan941.retrodromcompanion.network.cache.api.feed.FeedChannelItem
import org.leviathan941.retrodromcompanion.network.cache.internal.room.feed.RoomFeedDatabase

@Inject
@SingleIn(AppScope::class)
internal class RoomFeedCacheProviderImpl(
    private val database: RoomFeedDatabase,
) : FeedCacheProvider {

    override val categories: Flow<List<FeedCategory>>
        get() = database.categoriesDao().allFlow()

    override fun channelItemsPagingSource(channelUrl: String): PagingSource<Int, FeedChannelItem> =
        database.channelItemDao().pagingSource(channelUrl)

    override suspend fun findChannelItemByPostId(
        channelUrl: String,
        postId: String,
    ): FeedChannelItem? = withContext(Dispatchers.IO) {
        database.channelItemDao().findByPostId(
            channelUrl = channelUrl,
            postId = postId,
        )
    }

    override suspend fun channelItemsLastUpdatedMillis(channelUrl: String): Long? =
        withContext(Dispatchers.IO) {
            database.cacheMetadataDao().itemsLastUpdated(channelUrl)
        }

    override suspend fun channelItemsLastPageNumber(channelUrl: String): Int? =
        withContext(Dispatchers.IO) {
            database.channelItemDao().lastPageNumber(channelUrl)
        }
}
