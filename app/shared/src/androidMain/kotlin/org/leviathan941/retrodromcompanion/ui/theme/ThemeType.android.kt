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

package org.leviathan941.retrodromcompanion.ui.theme

import android.os.Build

internal actual fun platformThemeTypes(): List<ThemeType> = when {
    // Dynamic theme is supported from Android 12
    Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> ThemeType.entries

    // System theme is supported from Android 10
    Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q ->
        listOf(ThemeType.LIGHT, ThemeType.DARK, ThemeType.SYSTEM)

    else -> listOf(ThemeType.LIGHT, ThemeType.DARK)
}

internal actual val APP_THEME_DEFAULT: ThemeType = when {
    Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> ThemeType.DYNAMIC
    Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q -> ThemeType.SYSTEM
    else -> ThemeType.LIGHT
}
