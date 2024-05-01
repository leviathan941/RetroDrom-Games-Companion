/*
 * RetroDrom Games Companion
 * Copyright (C) 2024 Alexey Kuzin <amkuzink@gmail.com>.
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

package org.leviathan941.retrodromcompanion.rssreader

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import org.leviathan941.retrodromcompanion.rssreader.internal.PAGING_INITIAL_PAGE_NUMBER
import org.leviathan941.retrodromcompanion.rssreader.internal.RssFeedItemsSource

class RssFeedProvider(
    private val channelUrl: String,
) {
    val rssChannelItems: Flow<PagingData<RssChannelItem>> = Pager(
        config = PagingConfig(
            pageSize = 10,
            initialLoadSize = 20,
        ),
        pagingSourceFactory = {
            RssFeedItemsSource(channelUrl)
        },
        initialKey = PAGING_INITIAL_PAGE_NUMBER,
    ).flow
}
