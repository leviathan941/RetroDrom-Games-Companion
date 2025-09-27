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

import android.os.Build
import androidx.annotation.StringRes
import org.leviathan941.retrodromcompanion.R
import org.leviathan941.retrodromcompanion.ui.APP_THEME_DEFAULT

enum class ThemeType(
    val value: String,
) {
    LIGHT("light"),
    DARK("dark"),
    SYSTEM("system"),
    DYNAMIC("dynamic"),
    ;

    companion object {
        fun fromValue(value: String?): ThemeType =
            supportedEntries().firstOrNull { it.value == value } ?: APP_THEME_DEFAULT

        @StringRes
        fun ThemeType.toStringResource(): Int = when (this) {
            LIGHT -> R.string.theme_name_light
            DARK -> R.string.theme_name_dark
            SYSTEM -> R.string.theme_name_system
            DYNAMIC -> R.string.theme_name_dynamic
        }

        fun supportedEntries(): List<ThemeType> = when {
            // Dynamic theme is supported from Android 12
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> entries
            // System theme is supported from Android 10
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q -> listOf(LIGHT, DARK, SYSTEM)
            else -> listOf(LIGHT, DARK)
        }
    }
}
