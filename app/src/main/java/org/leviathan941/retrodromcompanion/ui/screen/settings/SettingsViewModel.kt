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

package org.leviathan941.retrodromcompanion.ui.screen.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.cancellable
import kotlinx.coroutines.launch
import org.leviathan941.retrodromcompanion.app.Singletons
import org.leviathan941.retrodromcompanion.ui.APP_THEME_DEFAULT
import org.leviathan941.retrodromcompanion.ui.theme.ThemeType

class SettingsViewModel : ViewModel() {
    private val _appTheme = MutableStateFlow(APP_THEME_DEFAULT)
    val appTheme: StateFlow<ThemeType> = _appTheme.asStateFlow()

    private val _newsPushEnabled = MutableStateFlow(false)
    val newsPushEnabled: StateFlow<Boolean> = _newsPushEnabled.asStateFlow()

    init {
        viewModelScope.launch {
            Singletons.preferencesRepository.ui
                .cancellable()
                .collect { uiPreferences ->
                    _appTheme.value = uiPreferences.appTheme
                    _newsPushEnabled.value = uiPreferences.newsPushEnabled
                }
        }
    }

    fun setAppTheme(appTheme: ThemeType) {
        viewModelScope.launch {
            Singletons.preferencesRepository.setAppTheme(appTheme)
        }
    }

    fun setNewsPushEnabled(enabled: Boolean) {
        viewModelScope.launch {
            Singletons.preferencesRepository.setNewsPushEnabled(enabled)
            // TODO: Subscribe to the firebase topic here
        }
    }
}
