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
import androidx.compose.ui.graphics.colorspace.ColorSpaces
import androidx.compose.ui.uikit.LocalUIViewController
import org.leviathan941.retrodromcompanion.browser.internal.SafariInAppBrowser
import platform.UIKit.UIColor

@Composable
public actual fun rememberInAppBrowser(toolbarColor: Color): InAppBrowser {
    val viewController = LocalUIViewController.current
    return remember(viewController, toolbarColor) {
        SafariInAppBrowser(viewController, toolbarColor.toUIColor())
    }
}

private fun Color.toUIColor(): UIColor = convert(ColorSpaces.Srgb).let {
    UIColor(
        red = it.red.toDouble(),
        green = it.green.toDouble(),
        blue = it.blue.toDouble(),
        alpha = it.alpha.toDouble(),
    )
}
