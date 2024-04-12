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
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.leviathan941.retrodromcompanion.R
import org.leviathan941.retrodromcompanion.network.wordpress.WpGetErrorException
import org.leviathan941.retrodromcompanion.network.wordpress.WpRetrofitClient
import org.leviathan941.retrodromcompanion.ui.BASE_TITLE
import org.leviathan941.retrodromcompanion.ui.BASE_URL
import org.leviathan941.retrodromcompanion.ui.MAIN_VIEW_TAG
import org.leviathan941.retrodromcompanion.ui.navigation.MainNavScreen
import org.leviathan941.retrodromcompanion.ui.screen.loading.LoadingState

class MainViewModel(
    context: Context,
) : ViewModel() {
    private val wpRetrofitClient = WpRetrofitClient(BASE_URL)

    private val _uiState = MutableStateFlow(
        MainViewState(
            loadingData = MainNavScreen.Loading(
                title = context.getString(R.string.loading_screen_title)
            ),
        )
    )
    val uiState: StateFlow<MainViewState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            _uiState.value = try {
                _uiState.value.copy(
                    loadingData = _uiState.value.loadingData.copy(
                        state = LoadingState.Success,
                    ),
                    rssFeedData = fetchRssScreens(),
                )
            } catch (e: WpGetErrorException) {
                Log.e(MAIN_VIEW_TAG, "Failed to fetch RSS categories", e)
                _uiState.value.copy(
                    loadingData = _uiState.value.loadingData.copy(
                        state = LoadingState.Failure(e.code, e.message.orEmpty())
                    )
                )
            }
        }
    }

    fun setWebViewData(screen: MainNavScreen.WebView) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                webViewData = screen,
            )
        }
    }

    @Throws(WpGetErrorException::class)
    private suspend fun fetchRssScreens(): List<MainNavScreen.RssFeed> {
        val rssCategories = wpRetrofitClient.fetchCategories()
            .filter { it.postsCount > 0 }
            .map {
                MainNavScreen.RssFeed(
                    title = it.name,
                    channelUrl = it.link,
                )
            }
        val mainCategory = MainNavScreen.RssFeed(
            title = BASE_TITLE,
            channelUrl = BASE_URL,
        )
        val allCategories = listOf(mainCategory) + rssCategories
        Log.d(MAIN_VIEW_TAG, "Fetched RSS categories: $allCategories")
        return allCategories
    }
}

class MainViewModelFactory(
    private val context: Context,
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MainViewModel(context) as T
    }
}
