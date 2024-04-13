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

package org.leviathan941.retrodromcompanion.ui.topbar

import androidx.compose.material3.DrawerState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.leviathan941.retrodromcompanion.ui.navigation.MainNavActions

class MainNavButtonHandler(
    private val navigationActions: MainNavActions,
    private val drawerState: DrawerState,
    private val coroutineScope: CoroutineScope,
) : TopBarNavButtonListener {
    override fun onClicked(button: TopBarNavButton) {
        when (button) {
            TopBarNavButton.BACK -> navigationActions.navigateBack()
            TopBarNavButton.CLOSE -> navigationActions.navigateUp()
            TopBarNavButton.DRAWER -> coroutineScope.launch {
                drawerState.open()
            }

            TopBarNavButton.NONE -> Unit
        }
    }
}
