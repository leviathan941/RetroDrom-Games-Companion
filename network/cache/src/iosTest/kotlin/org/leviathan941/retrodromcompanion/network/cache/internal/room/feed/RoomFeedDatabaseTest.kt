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

package org.leviathan941.retrodromcompanion.network.cache.internal.room.feed

import androidx.room3.Room
import androidx.room3.withWriteTransaction
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.leviathan941.retrodromcompanion.network.cache.api.feed.FeedChannelItem
import org.leviathan941.retrodromcompanion.network.cache.internal.room.feed.category.RoomFeedCategoryEntity
import org.leviathan941.retrodromcompanion.network.cache.internal.room.feed.channel.RoomFeedChannelItemEntity
import org.leviathan941.retrodromcompanion.network.cache.internal.room.feed.metadata.RoomFeedCacheMetadataEntity
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

private const val CHANNEL = "https://example.com/feed"

// Runs on iOS only: the bundled SQLite driver has no native library for the Android host JVM.
class RoomFeedDatabaseTest {
    // Built as the app builds it, but in memory, through the constructor Room generated for iOS.
    private val database = Room
        .inMemoryDatabaseBuilder(factory = RoomFeedDatabaseConstructor::initialize)
        .buildFeedDatabase()

    @AfterTest
    fun closeDatabase() {
        database.close()
    }

    @Test
    fun channelItemsRoundTripByPage() = runTest {
        val dao = database.channelItemDao()
        database.withWriteTransaction {
            dao.addAll(
                listOf(
                    item(pageNumber = 1, postId = "1", categories = listOf("News", "Retro")),
                    item(pageNumber = 2, postId = "2", categories = emptyList()),
                ),
            )
        }

        assertEquals(
            FeedChannelItem(
                title = "Title 1",
                link = "https://example.com/1",
                pubDate = "Mon, 02 Nov 2026 10:00:00 +0000",
                categories = persistentListOf("News", "Retro"),
                creator = "Author",
                description = "<p>Description</p>",
                postId = "1",
            ),
            dao.findByPostId(channelUrl = CHANNEL, postId = "1"),
        )
        assertEquals(persistentListOf(), dao.findByPostId(CHANNEL, "2")?.categories)
        assertEquals(2, dao.lastPageNumber(CHANNEL))

        dao.clearPage(channelUrl = CHANNEL, pageNumber = 2)
        assertEquals(1, dao.lastPageNumber(CHANNEL))
        assertNull(dao.findByPostId(CHANNEL, "2"))
    }

    @Test
    fun categoriesAndMetadataAreStored() = runTest {
        database.categoriesDao().addAll(
            listOf(
                RoomFeedCategoryEntity(
                    id = 7,
                    name = "Reviews",
                    link = CHANNEL,
                    postsCount = 3,
                ),
            ),
        )
        assertEquals(listOf("Reviews"), database.categoriesDao().allFlow().first().map { it.name })

        val metadata = database.cacheMetadataDao()
        assertNull(metadata.itemsLastUpdated(CHANNEL))
        metadata.upsert(RoomFeedCacheMetadataEntity(channelUrl = CHANNEL, itemsLastUpdated = 1L))
        metadata.upsert(RoomFeedCacheMetadataEntity(channelUrl = CHANNEL, itemsLastUpdated = 2L))
        assertEquals(2L, metadata.itemsLastUpdated(CHANNEL))
    }
}

private fun item(
    pageNumber: Int,
    postId: String,
    categories: List<String>,
) = RoomFeedChannelItemEntity(
    channelUrl = CHANNEL,
    pageNumber = pageNumber,
    title = "Title $postId",
    link = "https://example.com/$postId",
    pubDate = "Mon, 02 Nov 2026 10:00:00 +0000",
    categories = categories.toImmutableList(),
    creator = "Author",
    description = "<p>Description</p>",
    postId = postId,
)
