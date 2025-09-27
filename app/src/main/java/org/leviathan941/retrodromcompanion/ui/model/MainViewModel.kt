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

package org.leviathan941.retrodromcompanion.ui.model

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.leviathan941.retrodromcompanion.R
import org.leviathan941.retrodromcompanion.common.Constants
import org.leviathan941.retrodromcompanion.network.cache.api.feed.FeedCacheProvider
import org.leviathan941.retrodromcompanion.network.cache.api.feed.FeedCategory
import org.leviathan941.retrodromcompanion.ui.MAIN_RSS_FEED_ID
import org.leviathan941.retrodromcompanion.ui.MAIN_VIEW_TAG
import org.leviathan941.retrodromcompanion.ui.navigation.MainNavScreen
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    @param:ApplicationContext
    private val context: Context,
    private val feedCacheProvider: FeedCacheProvider,
) : ViewModel() {
    private val _uiState = MutableStateFlow(
        MainViewState.RssFeed(
            screenData = emptyMap(),
        ),
    )
    val uiState: StateFlow<MainViewState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            feedCacheProvider.categories.map {
                convertFeedCategories(it)
            }.collect { categories ->
                _uiState.value = MainViewState.RssFeed(categories)
            }
        }
        refreshRssFeedData()
    }

    fun refreshRssFeedData() {
        viewModelScope.launch {
            feedCacheProvider.refresh().onFailure { e ->
                Log.e(MAIN_VIEW_TAG, "Failed to fetch RSS categories", e)
            }
        }
    }

    private fun convertFeedCategories(
        feedCategories: List<FeedCategory>,
    ): Map<Int, MainNavScreen.RssFeed> {
        val rssCategoryScreens = feedCategories
            .filter { it.postsCount > 0 }
            .map {
                MainNavScreen.RssFeed(
                    id = it.id,
                    title = it.name,
                    channelUrl = it.link,
                )
            }
        val mainCategoryScreen = MainNavScreen.RssFeed(
            id = MAIN_RSS_FEED_ID,
            title = context.getString(R.string.main_rss_feed_title),
            channelUrl = Constants.RETRODROM_BASE_URL,
        )
        val allScreens = sequenceOf(mainCategoryScreen)
            .plus(rssCategoryScreens)
            .associateBy { it.id }
            .toMap()
        Log.d(MAIN_VIEW_TAG, "Fetched RSS categories: $allScreens")
        return allScreens
    }
}
