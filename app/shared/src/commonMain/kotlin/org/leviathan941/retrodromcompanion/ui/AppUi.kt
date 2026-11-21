/*
 * RetroDrom Games Companion
 * Copyright (C) 2026 Alexey Kuzin <amkuzink@gmail.com>.
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

package org.leviathan941.retrodromcompanion.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import dev.zacsweers.metrox.viewmodel.metroViewModel
import org.leviathan941.retrodromcompanion.ui.model.ViewModelKeys
import org.leviathan941.retrodromcompanion.ui.navigation.matchDeepLink
import org.leviathan941.retrodromcompanion.ui.theme.MainTheme
import org.leviathan941.retrodromcompanion.ui.theme.SecondThemeColorScheme
import org.leviathan941.retrodromcompanion.ui.theme.ThemeViewModel

/**
 * The app's whole UI: the theme and, once [isAppDataReady], the main view, opened at [deepLink]
 * when it names a destination.
 *
 * Nothing may read app data until [isAppDataReady]: a host that holds a splash screen over the
 * UI still composes it.
 */
@Composable
public fun AppUi(
    deepLink: String?,
    isAppDataReady: Boolean,
) {
    val themeViewModel = metroViewModel<ThemeViewModel>(
        key = ViewModelKeys.THEME_VIEW_MODEL,
    )
    val appTheme by themeViewModel.appTheme.collectAsState()
    val deepLinkDestination = remember(deepLink) {
        deepLink?.let { link ->
            matchDeepLink(link).also { mainViewLogger.d { "Handle deeplink $link: $it" } }
        }
    }

    MainTheme(
        selectedTheme = appTheme,
        materialColorSchemes = SecondThemeColorScheme,
    ) {
        if (isAppDataReady) {
            MainView(deepLinkDestination)
        }
    }
}
