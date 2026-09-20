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

package org.leviathan941.retrodromcompanion.network.cache.internal.room.feed.channel

import androidx.room3.ColumnInfo
import androidx.room3.Entity
import androidx.room3.Index
import androidx.room3.PrimaryKey
import kotlinx.collections.immutable.ImmutableList

@Entity(
    tableName = RoomFeedChannelItemTable.TABLE_NAME,
    indices = [
        Index(
            value = [
                RoomFeedChannelItemTable.COLUMN_CHANNEL_URL,
                RoomFeedChannelItemTable.COLUMN_PAGE_NUMBER,
            ],
        ),
        Index(
            value = [
                RoomFeedChannelItemTable.COLUMN_CHANNEL_URL,
                RoomFeedChannelItemTable.COLUMN_POST_ID,
            ],
        ),
    ],
)
internal data class RoomFeedChannelItemEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = RoomFeedChannelItemTable.COLUMN_AUTO_ID)
    val autoId: Long = 0,
    @ColumnInfo(name = RoomFeedChannelItemTable.COLUMN_CHANNEL_URL)
    val channelUrl: String,
    @ColumnInfo(name = RoomFeedChannelItemTable.COLUMN_PAGE_NUMBER)
    val pageNumber: Int,
    @ColumnInfo(name = RoomFeedChannelItemTable.COLUMN_TITLE)
    val title: String,
    @ColumnInfo(name = RoomFeedChannelItemTable.COLUMN_LINK)
    val link: String,
    @ColumnInfo(name = RoomFeedChannelItemTable.COLUMN_PUB_DATE)
    val pubDate: String,
    @ColumnInfo(name = RoomFeedChannelItemTable.COLUMN_CATEGORIES)
    val categories: ImmutableList<String>,
    @ColumnInfo(name = RoomFeedChannelItemTable.COLUMN_CREATOR)
    val creator: String,
    @ColumnInfo(name = RoomFeedChannelItemTable.COLUMN_DESCRIPTION)
    val description: String,
    @ColumnInfo(name = RoomFeedChannelItemTable.COLUMN_POST_ID)
    val postId: String,
)
