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

import androidx.room3.withWriteTransaction
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.leviathan941.retrodromcompanion.common.Clock
import org.leviathan941.retrodromcompanion.common.logging.Logger
import org.leviathan941.retrodromcompanion.network.cache.api.feed.FeedCacheMutator
import org.leviathan941.retrodromcompanion.network.cache.internal.room.feed.RoomFeedDatabase
import org.leviathan941.retrodromcompanion.network.cache.internal.room.feed.channel.RoomFeedChannelItemEntity
import org.leviathan941.retrodromcompanion.network.cache.internal.room.feed.metadata.RoomFeedCacheMetadataEntity
import org.leviathan941.retrodromcompanion.network.wordpress.WpNetworkClient

private val logger = Logger.withTag("RoomFeedCache")

private const val FIRST_PAGE_NUMBER = 1

@Inject
@SingleIn(AppScope::class)
internal class RoomFeedCacheMutatorImpl(
    private val wpNetworkClient: WpNetworkClient,
    private val database: RoomFeedDatabase,
    private val clock: Clock,
) : FeedCacheMutator {
    override suspend fun refreshCategories(): Result<Unit> = withContext(Dispatchers.IO) {
        wpNetworkClient.fetchCategories()
            .mapCatching { categories ->
                logger.d { "Fetched ${categories.size} categories from WP" }
                database.withWriteTransaction {
                    database.categoriesDao().run {
                        clear()
                        addAll(categories.map { it.toEntity() })
                    }
                }
            }
    }

    override suspend fun refreshChannelItems(channelUrl: String): Result<Int> =
        fetchChannelItemsPage(
            channelUrl = channelUrl,
            pageNumber = FIRST_PAGE_NUMBER,
        ).mapCatching { entities ->
            // A failed refresh must leave the previously cached feed readable.
            database.withWriteTransaction {
                database.channelItemDao().clearAll(channelUrl)
                database.channelItemDao().addAll(entities)
                database.cacheMetadataDao().upsert(
                    RoomFeedCacheMetadataEntity(
                        channelUrl = channelUrl,
                        itemsLastUpdated = clock.currentTimeMillis(),
                    ),
                )
            }
            entities.size
        }

    override suspend fun loadChannelItemsPage(
        channelUrl: String,
        pageNumber: Int,
    ): Result<Int> = fetchChannelItemsPage(
        channelUrl = channelUrl,
        pageNumber = pageNumber,
    ).mapCatching { entities ->
        database.withWriteTransaction {
            database.channelItemDao().clearPage(
                channelUrl = channelUrl,
                pageNumber = pageNumber,
            )
            database.channelItemDao().addAll(entities)
        }
        entities.size
    }

    private suspend fun fetchChannelItemsPage(
        channelUrl: String,
        pageNumber: Int,
    ): Result<List<RoomFeedChannelItemEntity>> = withContext(Dispatchers.IO) {
        wpNetworkClient.fetchRssFeedChannelPage(
            channelUrl = channelUrl,
            pageNumber = pageNumber,
        ).map { channel ->
            channel.items.mapNotNull { item ->
                item.toEntityOrNull(
                    channelUrl = channelUrl,
                    pageNumber = pageNumber,
                )
            }.also {
                logger.d { "Fetched ${it.size} items of $channelUrl page $pageNumber" }
            }
        }
    }
}
