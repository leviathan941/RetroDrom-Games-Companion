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
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.leviathan941.retrodromcompanion.rssreader.RssChannel

class RssFeedViewModel(
    channelUrl: String,
) : ViewModel() {
    private val feedProvider = RssFeedProvider(channelUrl)

    private val _uiState = MutableStateFlow(RssFeedViewState())
    val uiState: StateFlow<RssFeedViewState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(contentType = RssFeedContentType.LOADING)
            fetchChannel()?.let { channel ->
                _uiState.value =
                    RssFeedViewState(rssChannel = channel, contentType = RssFeedContentType.SHOW)
            } ?: run {
                _uiState.value = _uiState.value.copy(contentType = RssFeedContentType.ERROR)
            }
        }
    }

    fun refreshChannel(showIsRefreshing: Boolean = true) {
        viewModelScope.launch {
            if (showIsRefreshing) {
                _uiState.value = _uiState.value.copy(isRefreshing = true)
            }
            fetchChannel()?.let { channel ->
                _uiState.value = _uiState.value.copy(
                    rssChannel = channel,
                    contentType = RssFeedContentType.SHOW,
                    isRefreshing = false,
                )
            } ?: run {
                _uiState.value = _uiState.value.copy(
                    contentType = RssFeedContentType.ERROR,
                    isRefreshing = false,
                )
            }
        }
    }

    private suspend fun fetchChannel(useCache: Boolean = false): RssChannel? {
        return try {
            feedProvider.fetch(useCache)
        } catch (e: RssFeedProvider.FetchException) {
            if (!useCache) {
                // If fetching failed, try to use cache.
                fetchChannel(useCache = true)
            } else {
                null
            }
        }
    }
}

class RssFeedViewModelFactory(
    private val channelUrl: String,
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return RssFeedViewModel(channelUrl) as T
    }
}
