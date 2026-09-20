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

package org.leviathan941.retrodromcompanion.network.cache.internal.room.feed.metadata

import androidx.room3.ColumnInfo
import androidx.room3.Entity
import androidx.room3.PrimaryKey

@Entity(tableName = RoomFeedCacheMetadataTable.TABLE_NAME)
internal data class RoomFeedCacheMetadataEntity(
    @PrimaryKey
    @ColumnInfo(name = RoomFeedCacheMetadataTable.COLUMN_CHANNEL_URL)
    val channelUrl: String,
    @ColumnInfo(name = RoomFeedCacheMetadataTable.COLUMN_ITEMS_LAST_UPDATED)
    val itemsLastUpdated: Long,
)
