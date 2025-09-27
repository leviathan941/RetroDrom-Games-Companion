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

package org.leviathan941.retrodromcompanion.network.cache.internal.room.feed.category

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import org.leviathan941.retrodromcompanion.network.cache.api.feed.FeedCategory

@Entity(RoomFeedCategoryTable.TABLE_NAME)
internal data class RoomFeedCategoryEntity(
    @PrimaryKey(autoGenerate = true)
    val autoId: Int = 0,
    @ColumnInfo(name = RoomFeedCategoryTable.COLUMN_ID, typeAffinity = ColumnInfo.INTEGER)
    override val id: Int,
    @ColumnInfo(name = RoomFeedCategoryTable.COLUMN_NAME, typeAffinity = ColumnInfo.TEXT)
    override val name: String,
    @ColumnInfo(name = RoomFeedCategoryTable.COLUMN_LINK, typeAffinity = ColumnInfo.TEXT)
    override val link: String,
    @ColumnInfo(name = RoomFeedCategoryTable.COLUMN_POSTS_COUNT, typeAffinity = ColumnInfo.INTEGER)
    override val postsCount: Int,
) : FeedCategory
