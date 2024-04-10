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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.leviathan941.retrodromcompanion.ui.navigation.MainNavScreen

class MainViewModel : ViewModel() {
    private val screensProvider = MainScreenProvider(BASE_URL)

    private val _uiState = MutableStateFlow(
        MainViewState(
            allScreens = screensProvider.initialScreens,
            currentScreen = MainNavScreen.Loading,
        )
    )
    val uiState: StateFlow<MainViewState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            screensProvider.fetchScreens().let { screens ->
                updateScreens(
                    screens,
                    changeCurrentScreen = screens.find { it !is MainNavScreen.Loading },
                )
            }
        }
    }

    private fun updateScreens(
        screens: List<MainNavScreen>,
        changeCurrentScreen: MainNavScreen? = null,
    ) {
        _uiState.value = _uiState.value.copy(
            allScreens = screens,
            currentScreen = changeCurrentScreen ?: _uiState.value.currentScreen,
        )
    }

    private companion object {
        const val BASE_URL = "https://retrodrom.games/"
    }
}
