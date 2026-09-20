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

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.binding
import dev.zacsweers.metrox.viewmodel.ManualViewModelAssistedFactory
import dev.zacsweers.metrox.viewmodel.ManualViewModelAssistedFactoryKey
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.leviathan941.retrodromcompanion.rssreader.RssChannelItem
import org.leviathan941.retrodromcompanion.rssreader.RssFeedProvider
import org.leviathan941.retrodromcompanion.ui.rssScreenLogger

@AssistedInject
class RssFeedViewModel(
    @Assisted channelUrl: String,
    rssFeedProviderFactory: RssFeedProvider.Factory,
) : ViewModel() {
    private val rssFeedProvider = rssFeedProviderFactory.create(channelUrl)

    private val _rssChannelItems = MutableStateFlow<PagingData<RssChannelItem>>(PagingData.empty())
    val rssChannelItems: StateFlow<PagingData<RssChannelItem>> = _rssChannelItems.asStateFlow()

    init {
        rssScreenLogger.d { "Load RSS channel: $channelUrl" }
        viewModelScope.launch {
            rssFeedProvider.rssChannelItems
                .cachedIn(viewModelScope)
                .collect {
                    _rssChannelItems.value = it
                }
        }
    }

    @AssistedFactory
    @ManualViewModelAssistedFactoryKey
    @ContributesIntoMap(AppScope::class, binding<ManualViewModelAssistedFactory>())
    interface Factory : ManualViewModelAssistedFactory {
        fun create(channelUrl: String): RssFeedViewModel
    }
}
