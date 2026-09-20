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

package org.leviathan941.retrodromcompanion.rssreader.internal

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.leviathan941.retrodromcompanion.common.Clock
import org.leviathan941.retrodromcompanion.network.cache.api.feed.FeedCacheMutator
import org.leviathan941.retrodromcompanion.network.cache.api.feed.FeedCacheProvider
import org.leviathan941.retrodromcompanion.rssreader.RssChannelItem
import org.leviathan941.retrodromcompanion.rssreader.RssFeedProvider

@OptIn(ExperimentalPagingApi::class)
@AssistedInject
internal class RssFeedProviderImpl(
    @Assisted private val channelUrl: String,
    private val cacheProvider: FeedCacheProvider,
    private val cacheMutator: FeedCacheMutator,
    private val clock: Clock,
) : RssFeedProvider {

    override val rssChannelItems: Flow<PagingData<RssChannelItem>> = Pager(
        config = PagingConfig(
            pageSize = FEED_PAGING_PAGE_SIZE,
            // Room knows the item count, so Paging would otherwise emit null placeholders.
            enablePlaceholders = false,
            initialLoadSize = FEED_PAGING_INITIAL_LOAD_SIZE,
        ),
        remoteMediator = RssFeedItemsMediator(
            channelUrl = channelUrl,
            cacheProvider = cacheProvider,
            cacheMutator = cacheMutator,
            clock = clock,
        ),
        pagingSourceFactory = {
            cacheProvider.channelItemsPagingSource(channelUrl)
        },
    ).flow.map { pagingData ->
        pagingData.map { it.toPublic() }
    }

    override suspend fun findByPostId(postId: String): RssChannelItem? {
        findCachedByPostId(postId)?.let { return it }
        // A push notification can arrive before the channel has ever been cached, or point at a
        // post published since the last refresh, so give the cache one chance to catch up.
        return if (cacheMutator.refreshChannelItems(channelUrl).isSuccess) {
            findCachedByPostId(postId)
        } else {
            null
        }
    }

    private suspend fun findCachedByPostId(postId: String): RssChannelItem? =
        cacheProvider.findChannelItemByPostId(
            channelUrl = channelUrl,
            postId = postId,
        )?.toPublic()

    @AssistedFactory
    internal interface Factory {
        fun create(channelUrl: String): RssFeedProviderImpl
    }

    private companion object {
        private const val FEED_PAGING_PAGE_SIZE = 10
        private const val FEED_PAGING_INITIAL_LOAD_SIZE = 20
    }
}
