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

import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// iOS has no dynamic color.
@Composable
internal actual fun dynamicColorScheme(isDarkTheme: Boolean): ColorScheme? = null

// Nothing to set: Compose Multiplatform's view controller already styles the status bar to match
// the app's theme, also when the app and the system are in opposite modes (seen in C1).
@Composable
internal actual fun StatusBarStyle(
    color: Color,
    isDarkTheme: Boolean,
): Unit = Unit
