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

package org.leviathan941.retrodromcompanion.browser.internal

import org.leviathan941.retrodromcompanion.browser.InAppBrowser
import platform.Foundation.NSURL
import platform.SafariServices.SFSafariViewController
import platform.SafariServices.SFSafariViewControllerConfiguration
import platform.UIKit.UIColor
import platform.UIKit.UIViewController

// Presents over [presenter], so that dismissing Safari returns to the screen it was opened from.
internal class SafariInAppBrowser(
    private val presenter: UIViewController,
    private val toolbarColor: UIColor,
) : InAppBrowser {
    override fun open(url: String): Boolean {
        // SFSafariViewController raises NSInvalidArgumentException for any scheme but http(s).
        val nsUrl = NSURL.URLWithString(url)
            ?.takeIf { it.scheme?.lowercase() in WEB_SCHEMES }
            ?: return false
        val configuration = SFSafariViewControllerConfiguration().apply {
            barCollapsingEnabled = true
        }
        val safari = SFSafariViewController(uRL = nsUrl, configuration = configuration).apply {
            preferredBarTintColor = toolbarColor
        }
        presenter.presentViewController(safari, animated = true, completion = null)
        return true
    }

    private companion object {
        val WEB_SCHEMES = setOf("http", "https")
    }
}
