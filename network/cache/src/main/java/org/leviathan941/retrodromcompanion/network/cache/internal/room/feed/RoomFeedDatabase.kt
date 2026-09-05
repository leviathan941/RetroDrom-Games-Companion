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

package org.leviathan941.retrodromcompanion.network.cache.internal.room.feed

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import org.leviathan941.retrodromcompanion.network.cache.internal.room.feed.category.RoomFeedCategoryDao
import org.leviathan941.retrodromcompanion.network.cache.internal.room.feed.category.RoomFeedCategoryEntity
import org.leviathan941.retrodromcompanion.network.cache.internal.room.feed.channel.RoomFeedChannelItemDao
import org.leviathan941.retrodromcompanion.network.cache.internal.room.feed.channel.RoomFeedChannelItemEntity
import org.leviathan941.retrodromcompanion.network.cache.internal.room.feed.metadata.RoomFeedCacheMetadataDao
import org.leviathan941.retrodromcompanion.network.cache.internal.room.feed.metadata.RoomFeedCacheMetadataEntity

internal const val FEED_CACHE_DATABASE_NAME: String = "feed_cache.db"
private const val DB_VERSION = 2

@Database(
    entities = [
        RoomFeedCategoryEntity::class,
        RoomFeedChannelItemEntity::class,
        RoomFeedCacheMetadataEntity::class,
    ],
    version = DB_VERSION,
    exportSchema = true,
    autoMigrations = [
        AutoMigration(from = 1, to = 2),
    ],
)
@TypeConverters(RoomFeedTypeConverters::class)
internal abstract class RoomFeedDatabase : RoomDatabase() {
    internal abstract fun categoriesDao(): RoomFeedCategoryDao

    internal abstract fun channelItemDao(): RoomFeedChannelItemDao

    internal abstract fun cacheMetadataDao(): RoomFeedCacheMetadataDao
}
