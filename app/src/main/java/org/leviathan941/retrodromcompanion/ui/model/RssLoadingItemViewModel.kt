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

package org.leviathan941.retrodromcompanion.ui.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.leviathan941.retrodromcompanion.rssreader.RssItemFinder
import org.leviathan941.retrodromcompanion.ui.navigation.RssFeedDestination

@HiltViewModel(assistedFactory = RssLoadingItemViewModel.Factory::class)
class RssLoadingItemViewModel @AssistedInject constructor(
    @Assisted private val rssLoadingItem: RssFeedDestination.LoadingItem,
) : ViewModel() {
    private val rssItemFinder = RssItemFinder(
        channelUrl = rssLoadingItem.channelUrl,
    )

    private val _viewState = MutableStateFlow<RssLoadingItemViewState>(
        RssLoadingItemViewState.Loading
    )
    val viewState: StateFlow<RssLoadingItemViewState> = _viewState.asStateFlow()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            _viewState.value = rssItemFinder.findByPostId(rssLoadingItem.postId)?.let {
                RssLoadingItemViewState.Success(
                    item = it
                )
            } ?: RssLoadingItemViewState.Error
        }
    }

    fun cancel() {
        viewModelScope.cancel()
    }

    @AssistedFactory
    interface Factory {
        fun create(
            rssLoadingItem: RssFeedDestination.LoadingItem,
        ): RssLoadingItemViewModel
    }
}