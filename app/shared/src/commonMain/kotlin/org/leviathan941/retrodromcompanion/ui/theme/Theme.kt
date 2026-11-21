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

package org.leviathan941.retrodromcompanion.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
internal fun MainTheme(
    materialColorSchemes: MaterialColorSchemes,
    selectedTheme: ThemeType,
    content: @Composable () -> Unit,
) {
    val isSystemDark = isSystemInDarkTheme()
    val dynamicScheme = if (selectedTheme == ThemeType.DYNAMIC) {
        dynamicColorScheme(isSystemDark)
    } else {
        null
    }
    val (colorScheme, isDarkTheme) = when {
        dynamicScheme != null -> dynamicScheme to isSystemDark

        selectedTheme == ThemeType.SYSTEM -> if (isSystemDark) {
            materialColorSchemes.darkScheme to true
        } else {
            materialColorSchemes.lightScheme to false
        }

        selectedTheme == ThemeType.DARK -> materialColorSchemes.darkScheme to true

        selectedTheme == ThemeType.LIGHT -> materialColorSchemes.lightScheme to false

        else -> materialColorSchemes.lightScheme to false
    }
    StatusBarStyle(
        color = colorScheme.primaryContainer,
        isDarkTheme = isDarkTheme,
    )

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content,
    )
}

/**
 * The platform's dynamic color scheme for the [isDarkTheme] variant, or `null` when the platform
 * has none.
 */
@Composable
internal expect fun dynamicColorScheme(isDarkTheme: Boolean): ColorScheme?

/**
 * Styles the status bar to match the app's top bar: [color] behind it, and icons readable on a
 * dark or light background per [isDarkTheme].
 */
@Composable
internal expect fun StatusBarStyle(
    color: Color,
    isDarkTheme: Boolean,
)
