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
import kotlinx.coroutines.Job
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.leviathan941.retrodromcompanion.rssreader.RssChannel
import org.leviathan941.retrodromcompanion.ui.RSS_SCREEN_TAG

class RssFeedViewModel : ViewModel() {
    private val loadedChannels = mutableSetOf<String>()

    private val _uiState = MutableStateFlow(RssFeedViewState())
    val uiState: StateFlow<RssFeedViewState> = _uiState.asStateFlow()

    private var fetchJob: Job? = null

    fun setChannel(channelUrl: String) {
        val useCache = channelUrl in loadedChannels
        Log.d(RSS_SCREEN_TAG, "Load RSS channel: $channelUrl, useCache: $useCache")
        launchFetch {
            _uiState.value = _uiState.value.copy(
                contentType = RssFeedContentType.LOADING,
                rssChannel = null,
            )
            fetchChannel(channelUrl, useCache)
                .takeIf { currentCoroutineContext().isActive }?.let {
                    onFetchFinished(it)
                }
        }
    }

    fun refreshChannel(
        channelUrl: String,
        showIsRefreshing: Boolean = false
    ) {
        Log.d(RSS_SCREEN_TAG, "Refresh RSS channel: $channelUrl")
        launchFetch {
            if (showIsRefreshing) {
                _uiState.value = _uiState.value.copy(isRefreshing = true)
            }
            fetchChannel(channelUrl).takeIf { currentCoroutineContext().isActive }?.let {
                onFetchFinished(it)
            }
        }
    }

    private inline fun launchFetch(crossinline block: suspend () -> Unit) {
        fetchJob?.cancel()
        fetchJob = viewModelScope.launch {
            block()
        }
    }

    private fun onFetchFinished(channel: RssChannel?) {
        if (channel == null) {
            _uiState.value = _uiState.value.copy(
                contentType = RssFeedContentType.ERROR,
                isRefreshing = false,
            )
        } else {
            loadedChannels.add(channel.link)
            _uiState.value = _uiState.value.copy(
                rssChannel = channel,
                contentType = RssFeedContentType.SHOW,
                isRefreshing = false,
            )
        }
    }

    private suspend fun fetchChannel(
        channelUrl: String,
        useCache: Boolean = false
    ): RssChannel? {
        return try {
            RssFeedProvider(channelUrl).fetch(useCache)
        } catch (e: RssFeedProvider.FetchException) {
            if (!useCache) {
                // If fetching failed, try to use cache.
                fetchChannel(channelUrl, useCache = true)
            } else {
                null
            }
        }
    }
}
