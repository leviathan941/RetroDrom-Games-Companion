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

enum class ThemeType(val value: String) {
    LIGHT("light"),
    DARK("dark"),
    SYSTEM("system"),
    DYNAMIC("dynamic"),
    ;

    companion object {
        fun fromValue(value: String): ThemeType? {
            return supportedEntries().firstOrNull { it.value == value }
        }

        @StringRes
        fun ThemeType.toStringResource(): Int {
            return when (this) {
                LIGHT -> R.string.theme_name_light
                DARK -> R.string.theme_name_dark
                SYSTEM -> R.string.theme_name_system
                DYNAMIC -> R.string.theme_name_dynamic
            }
        }

        fun supportedEntries(): List<ThemeType> {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                entries
            } else {
                listOf(LIGHT, DARK, SYSTEM)
            }
        }
    }
}
