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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.leviathan941.retrodromcompanion.rssreader.RssChannel
import org.leviathan941.retrodromcompanion.ui.RSS_SCREEN_TAG
import org.leviathan941.retrodromcompanion.ui.screen.loading.LoadingState
import kotlin.coroutines.coroutineContext

class RssFeedViewModel : ViewModel() {
    private val loadedChannels = mutableSetOf<String>()

    private val _uiState = MutableStateFlow(RssFeedViewState())
    val uiState: StateFlow<RssFeedViewState> = _uiState.asStateFlow()

    private var fetchJob: Job? = null

    fun loadChannel(channelUrl: String) {
        Log.d(RSS_SCREEN_TAG, "Load RSS channel: $channelUrl")
        launchFetchJob {
            _uiState.value = _uiState.value.copy(
                loadingState = LoadingState.InProgress,
                rssChannel = null,
                isRefreshing = false,
            )
            runFetch(channelUrl, useCache = true)
        }
    }

    fun refreshChannel(
        channelUrl: String,
        showIsRefreshing: Boolean = false
    ) {
        Log.d(RSS_SCREEN_TAG, "Refresh RSS channel: $channelUrl")
        launchFetchJob {
            if (showIsRefreshing) {
                _uiState.value = _uiState.value.copy(isRefreshing = true)
            }
            runFetch(channelUrl, useCache = false)
        }
    }

    private inline fun launchFetchJob(crossinline block: suspend () -> Unit) {
        fetchJob?.cancel()
        fetchJob = viewModelScope.launch {
            block()
        }
    }

    private suspend fun runFetch(channelUrl: String, useCache: Boolean) {
        try {
            fetchChannel(channelUrl, useCache)?.let {
                onFetched(it, fromCache = useCache)
            } ?: onFetchFailed("Failed to fetch RSS channel by unknown reason")
        } catch (e: RssFeedProvider.FetchException) {
            if (coroutineContext.isActive) {
                onFetchException(e)
            }
        }
    }

    private fun onFetched(
        channel: RssChannel,
        fromCache: Boolean,
    ) {
        val shouldRefresh = fromCache && channel.link !in loadedChannels
        loadedChannels.add(channel.link)
        _uiState.value = _uiState.value.copy(
            rssChannel = channel,
            loadingState = LoadingState.Success,
            isRefreshing = shouldRefresh,
        )
    }

    private fun onFetchException(e: RssFeedProvider.FetchException) {
        onFetchFailed(e.message ?: "Unknown error: $e")
    }

    private fun onFetchFailed(message: String) {
        Log.e(RSS_SCREEN_TAG, "Exception during RSS channel fetch: $message")
        _uiState.value = _uiState.value.copy(
            rssChannel = null,
            loadingState = LoadingState.Failure(
                message = message,
            ),
            isRefreshing = false,
        )
    }

    @Throws(RssFeedProvider.FetchException::class)
    private suspend fun fetchChannel(
        channelUrl: String,
        useCache: Boolean,
    ): RssChannel? {
        return try {
            RssFeedProvider(channelUrl).fetch(useCache)
        } catch (e: RssFeedProvider.FetchException) {
            if (useCache) {
                fetchChannel(channelUrl, useCache = false)
            } else {
                throw e
            }
        }
    }
}
