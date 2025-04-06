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

package org.leviathan941.retrodromcompanion.rssreader.internal

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import org.leviathan941.retrodromcompanion.rssreader.RssChannelItem

class RssFeedItemsSource(
    private val channelUrl: String,
) : PagingSource<Int, RssChannelItem>() {

    init {
        registerInvalidatedCallback {
            Log.d(RSS_READER_TAG, "Clear RSS feed cache for $channelUrl")
            RssFetcher.clearCache()
        }
    }

    override fun getRefreshKey(state: PagingState<Int, RssChannelItem>): Int {
        return ((state.anchorPosition ?: 0) - state.config.initialLoadSize / 2)
            .coerceAtLeast(PAGING_INITIAL_PAGE_NUMBER)
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, RssChannelItem> {
        return loadInternal(params, useCache = true)
    }

    private suspend fun loadInternal(
        params: LoadParams<Int>,
        useCache: Boolean,
    ): LoadResult<Int, RssChannelItem> {
        val pageNumber = params.key ?: PAGING_INITIAL_PAGE_NUMBER
        return try {
            Log.d(RSS_READER_TAG, "Loading RSS feed items for page $pageNumber, useCache=$useCache")
            val channelItems = fetchFeed(pageNumber, useCache)
            LoadResult.Page(
                data = channelItems.mapNotNull { it.toPublic() },
                prevKey = pageNumber.takeIf { it > 1 }?.let { it - 1 },
                nextKey = pageNumber.takeUnless { channelItems.isEmpty() }?.let { it + 1 },
            )
        } catch (e: Exception) {
            Log.e(
                RSS_READER_TAG,
                "Failed to load RSS feed items for page $pageNumber, useCache=$useCache", e
            )
            if (useCache && params is LoadParams.Refresh) {
                loadInternal(params, useCache = false)
            } else {
                LoadResult.Error(e)
            }
        }
    }

    private suspend fun fetchFeed(
        pageNumber: Int,
        useCache: Boolean,
    ): List<ParsedRssItem> {
        return RssFetcher.fetchFeedPage(
            channelUrl,
            pageNumber,
            useCache,
        ).items ?: emptyList()
    }
}
