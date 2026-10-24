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

package org.leviathan941.retrodromcompanion.browser

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import org.leviathan941.retrodromcompanion.browser.internal.CustomTabsInAppBrowser

/**
 * Returns an [InAppBrowser] whose toolbar is drawn in [toolbarColor].
 */
@Composable
public fun rememberInAppBrowser(toolbarColor: Color): InAppBrowser {
    val context = LocalContext.current
    return remember(context, toolbarColor) {
        CustomTabsInAppBrowser(context, toolbarColor.toArgb())
    }
}
