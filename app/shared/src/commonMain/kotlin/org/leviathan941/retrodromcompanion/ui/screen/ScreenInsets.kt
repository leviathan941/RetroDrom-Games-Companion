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

package org.leviathan941.retrodromcompanion.ui.screen

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.only
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.runtime.Composable

// A screen's Scaffold insets its content on every side but the bottom. The scrolling content
// takes the bottom inset as its own content padding instead, so it scrolls on under the home
// indicator or the navigation bar rather than stopping short of it at a hard edge.

/** The `contentWindowInsets` of a screen's Scaffold: all but the bottom. */
@Composable
internal fun screenContentWindowInsets(): WindowInsets =
    ScaffoldDefaults.contentWindowInsets.only(WindowInsetsSides.Top + WindowInsetsSides.Horizontal)

/** The content padding of a screen's scrolling content: the bottom inset its Scaffold leaves. */
@Composable
internal fun scrollingContentPadding(): PaddingValues =
    ScaffoldDefaults.contentWindowInsets.only(WindowInsetsSides.Bottom).asPaddingValues()
