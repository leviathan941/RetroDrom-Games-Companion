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

package org.leviathan941.retrodromcompanion.app.ios

import androidx.compose.ui.platform.UriHandler
import platform.Foundation.NSURL
import platform.UIKit.UIApplication

/**
 * A [UriHandler] that keeps the interface's contract on iOS: it throws [IllegalArgumentException]
 * when no app can open the URI. Compose Multiplatform's own iOS handler passes `openURL` no
 * completion handler, so a URI nothing opens, such as `mailto:` without Mail, fails silently.
 *
 * `canOpenURL` answers only for the schemes `Info.plist` lists under
 * `LSApplicationQueriesSchemes`; any other scheme counts as one no app can open.
 */
internal object IosUriHandler : UriHandler {
    override fun openUri(uri: String) {
        val url = NSURL.URLWithString(uri)
        val application = UIApplication.sharedApplication
        require(url != null && application.canOpenURL(url)) { "No app can open $uri" }
        application.openURL(url, options = emptyMap<Any?, Any>(), completionHandler = null)
    }
}
