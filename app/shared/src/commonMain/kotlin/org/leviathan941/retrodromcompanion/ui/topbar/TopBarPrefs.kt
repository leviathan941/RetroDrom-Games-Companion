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

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

public data class TopBarPrefs(
    public val title: String = "",
    public val subtitle: String = "",
    public val navButton: TopBarNavButton = TopBarNavButton.NONE,
    public val actions: ImmutableList<TopBarAction> = persistentListOf(),
)

public enum class TopBarNavButton {
    NONE,
    BACK,
    CLOSE,
    DRAWER,
}

public sealed interface TopBarAction {
    public data class Browse(
        public val url: String,
    ) : TopBarAction
}
