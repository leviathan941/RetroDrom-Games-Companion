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
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.leviathan941.retrodromcompanion.R
import org.leviathan941.retrodromcompanion.ui.BASE_TITLE
import org.leviathan941.retrodromcompanion.ui.BASE_URL
import org.leviathan941.retrodromcompanion.ui.navigation.MainNavScreen

class MainViewModel(
    context: Context,
) : ViewModel() {
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
            // TODO: Fetch the real categories.
            val rssFeedScreen = MainNavScreen.RssFeed(
                title = BASE_TITLE,
                channelUrl = BASE_URL,
            )
            _uiState.value = _uiState.value.copy(
                rssFeedData = listOf(rssFeedScreen),
            )
        }
    }

    fun setWebViewData(screen: MainNavScreen.WebView) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                webViewData = screen,
            )
        }
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
