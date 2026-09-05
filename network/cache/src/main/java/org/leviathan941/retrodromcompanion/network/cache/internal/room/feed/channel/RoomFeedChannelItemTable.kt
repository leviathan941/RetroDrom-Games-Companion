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

internal object RoomFeedChannelItemTable {
    const val TABLE_NAME: String = "feed_channel_items"

    const val COLUMN_AUTO_ID: String = "auto_id"
    const val COLUMN_CHANNEL_URL: String = "channel_url"
    const val COLUMN_PAGE_NUMBER: String = "page_number"
    const val COLUMN_TITLE: String = "title"
    const val COLUMN_LINK: String = "link"
    const val COLUMN_PUB_DATE: String = "pub_date"
    const val COLUMN_CATEGORIES: String = "categories"
    const val COLUMN_CREATOR: String = "creator"
    const val COLUMN_DESCRIPTION: String = "description"
    const val COLUMN_POST_ID: String = "post_id"
}
