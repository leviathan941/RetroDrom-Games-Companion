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

package org.leviathan941.retrodromcompanion

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import org.leviathan941.retrodromcompanion.notification.extractDeeplink
import org.leviathan941.retrodromcompanion.ui.MainView
import org.leviathan941.retrodromcompanion.ui.model.ViewModelKeys
import org.leviathan941.retrodromcompanion.ui.theme.MainTheme
import org.leviathan941.retrodromcompanion.ui.theme.SecondThemeColorScheme
import org.leviathan941.retrodromcompanion.ui.theme.ThemeViewModel

private const val TAG = "MainActivity"

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val themeViewModel = hiltViewModel<ThemeViewModel>(
                key = ViewModelKeys.THEME_VIEW_MODEL,
            )
            val appTheme by themeViewModel.appTheme.collectAsState()
            val navController = rememberNavController()

            MainTheme(
                selectedTheme = appTheme,
                materialColorSchemes = SecondThemeColorScheme,
            ) {
                MainView(navController)
            }

            LaunchedEffect(Unit) {
                extractDeeplink(intent)?.let {
                    Log.d(TAG, "Handle deeplink: $it")
                    navController.navigate(it)
                }
            }
        }
    }
}
