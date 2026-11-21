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

import org.jetbrains.compose.resources.StringResource
import org.leviathan941.retrodromcompanion.resources.Res
import org.leviathan941.retrodromcompanion.resources.theme_name_dark
import org.leviathan941.retrodromcompanion.resources.theme_name_dynamic
import org.leviathan941.retrodromcompanion.resources.theme_name_light
import org.leviathan941.retrodromcompanion.resources.theme_name_system

internal enum class ThemeType(
    val value: String,
) {
    LIGHT("light"),
    DARK("dark"),
    SYSTEM("system"),
    DYNAMIC("dynamic"),
    ;

    companion object {
        internal fun fromValue(value: String?): ThemeType =
            supportedEntries().firstOrNull { it.value == value } ?: APP_THEME_DEFAULT

        fun ThemeType.toStringResource(): StringResource = when (this) {
            LIGHT -> Res.string.theme_name_light
            DARK -> Res.string.theme_name_dark
            SYSTEM -> Res.string.theme_name_system
            DYNAMIC -> Res.string.theme_name_dynamic
        }

        fun supportedEntries(): List<ThemeType> = platformThemeTypes()
    }
}

/** The themes this platform can apply, in the order the settings list them. */
internal expect fun platformThemeTypes(): List<ThemeType>

/** The theme used until the user picks one, or when the stored one is not supported. */
internal expect val APP_THEME_DEFAULT: ThemeType
