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
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import org.leviathan941.retrodromcompanion.common.Clock
import org.leviathan941.retrodromcompanion.common.logging.Logger
import org.leviathan941.retrodromcompanion.network.cache.api.feed.FeedCacheMutator
import org.leviathan941.retrodromcompanion.network.cache.api.feed.FeedCacheProvider
import org.leviathan941.retrodromcompanion.network.cache.api.feed.FeedChannelItem
import java.util.concurrent.TimeUnit

private val logger = Logger.withTag("RssReader")

@OptIn(ExperimentalPagingApi::class)
internal class RssFeedItemsMediator(
    private val channelUrl: String,
    private val cacheProvider: FeedCacheProvider,
    private val cacheMutator: FeedCacheMutator,
    private val clock: Clock,
) : RemoteMediator<Int, FeedChannelItem>() {
    override suspend fun initialize(): InitializeAction {
        val lastUpdatedMillis = cacheProvider.channelItemsLastUpdatedMillis(channelUrl) ?: 0L
        val ageMillis = clock.currentTimeMillis() - lastUpdatedMillis
        logger.d { "Cached items of $channelUrl are $ageMillis ms old" }
        return if (ageMillis >= FEED_EXPIRED_TIME_MILLIS) {
            InitializeAction.LAUNCH_INITIAL_REFRESH
        } else {
            InitializeAction.SKIP_INITIAL_REFRESH
        }
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, FeedChannelItem>,
    ): MediatorResult = when (loadType) {
        // The feed is only ever extended towards older posts.
        LoadType.PREPEND -> MediatorResult.Success(endOfPaginationReached = true)

        LoadType.REFRESH -> {
            logger.d { "Refreshing RSS feed items of $channelUrl" }
            cacheMutator.refreshChannelItems(channelUrl).toMediatorResult()
        }

        LoadType.APPEND -> {
            val pageNumber = cacheProvider.channelItemsLastPageNumber(channelUrl)
                ?.plus(1)
                ?: FEED_PAGING_INITIAL_PAGE_NUMBER
            logger.d { "Loading RSS feed items of $channelUrl page $pageNumber" }
            cacheMutator.loadChannelItemsPage(
                channelUrl = channelUrl,
                pageNumber = pageNumber,
            ).toMediatorResult()
        }
    }

    private fun Result<Int>.toMediatorResult(): MediatorResult = fold(
        onSuccess = { loadedCount ->
            MediatorResult.Success(endOfPaginationReached = loadedCount <= 0)
        },
        onFailure = { error ->
            logger.e(error) { "Failed to load RSS feed items of $channelUrl" }
            MediatorResult.Error(error)
        },
    )

    private companion object {
        val FEED_EXPIRED_TIME_MILLIS = TimeUnit.HOURS.toMillis(1L)
    }
}
