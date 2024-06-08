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

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.leviathan941.retrodromcompanion.R
import org.leviathan941.retrodromcompanion.network.wordpress.WpGetErrorException
import org.leviathan941.retrodromcompanion.network.wordpress.WpRetrofitClient
import org.leviathan941.retrodromcompanion.ui.BASE_URL
import org.leviathan941.retrodromcompanion.ui.MAIN_RSS_FEED_ID
import org.leviathan941.retrodromcompanion.ui.MAIN_VIEW_TAG
import org.leviathan941.retrodromcompanion.ui.navigation.MainNavScreen
import org.leviathan941.retrodromcompanion.ui.screen.loading.LoadingState

class MainViewModel(
    application: Application,
) : AndroidViewModel(application) {
    private val wpRetrofitClient = WpRetrofitClient(BASE_URL)

    private val _uiState = MutableStateFlow(
        MainViewState(
            loadingData = MainNavScreen.Loading(
                title = application.getString(R.string.loading_screen_title)
            ),
            somethingWrongData = MainNavScreen.SomethingWrong(
                title = application.getString(R.string.something_wrong_screen_title),
            ),
            settingsData = MainNavScreen.Settings(
                title = application.getString(R.string.settings_screen_title),
            ),
        )
    )
    val uiState: StateFlow<MainViewState> = _uiState.asStateFlow()

    init {
        fetchRssData()
    }

    fun fetchRssData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                loadingData = _uiState.value.loadingData.copy(
                    state = LoadingState.InProgress,
                ),
            )
            _uiState.value = try {
                _uiState.value.copy(
                    loadingData = _uiState.value.loadingData.copy(
                        state = LoadingState.Success,
                    ),
                    rssFeedData = fetchAllRssScreens(),
                )
            } catch (e: WpGetErrorException) {
                Log.e(MAIN_VIEW_TAG, "Failed to fetch RSS categories", e)
                _uiState.value.copy(
                    loadingData = _uiState.value.loadingData.copy(
                        state = LoadingState.Failure(e.message.orEmpty())
                    )
                )
            }
        }
    }

    @Throws(WpGetErrorException::class)
    private suspend fun fetchAllRssScreens(): Map<Int, MainNavScreen.RssFeed> {
        val rssCategoryScreens = wpRetrofitClient.fetchCategories()
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
            title = getApplication<Application>().getString(R.string.main_rss_feed_title),
            channelUrl = BASE_URL,
        )
        val allScreens = sequenceOf(mainCategoryScreen)
            .plus(rssCategoryScreens)
            .associateBy { it.id }
            .toMap()
        Log.d(MAIN_VIEW_TAG, "Fetched RSS categories: $allScreens")
        return allScreens
    }
}

class MainViewModelFactory(
    application: Application,
) : ViewModelProvider.AndroidViewModelFactory(application)
