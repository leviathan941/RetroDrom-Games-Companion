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

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.leviathan941.retrodromcompanion.rssreader.RssChannelItem
import org.leviathan941.retrodromcompanion.rssreader.RssFeedProvider
import org.leviathan941.retrodromcompanion.ui.RSS_SCREEN_TAG

@HiltViewModel(assistedFactory = RssFeedViewModel.Factory::class)
class RssFeedViewModel @AssistedInject constructor(
    @Assisted channelUrl: String,
) : ViewModel() {
    private val rssFeedProvider = RssFeedProvider(channelUrl)

    private val _rssChannelItems = MutableStateFlow<PagingData<RssChannelItem>>(PagingData.empty())
    val rssChannelItems: StateFlow<PagingData<RssChannelItem>> = _rssChannelItems.asStateFlow()

    init {
        Log.d(RSS_SCREEN_TAG, "Load RSS channel: $channelUrl")
        viewModelScope.launch {
            rssFeedProvider.rssChannelItems
                .cachedIn(viewModelScope)
                .collect {
                    _rssChannelItems.value = it
                }
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(channelUrl: String): RssFeedViewModel
    }
}

