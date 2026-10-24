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

package org.leviathan941.retrodromcompanion.notification

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

/**
 * Pins the feed-item deeplink to the shape `android.net.Uri.Builder` used to produce, which the
 * `navDeepLink` on the loading-item destination still matches.
 */
class NotificationUtilsTest {

    @Test
    fun `feed item deeplink encodes the channel url as a query parameter`() {
        assertEquals(
            "retrodrom://feed/item/1001?channelUrl=https%3A%2F%2Fretrodrom.games%2F",
            createFeedItemDeeplink("1001"),
        )
    }

    @Test
    fun `feed item deeplink percent-encodes the post id as utf-8`() {
        assertEquals(
            "retrodrom://feed/item/a%2Fb%20%D0%AF-._~?channelUrl=https%3A%2F%2Fretrodrom.games%2F",
            createFeedItemDeeplink("a/b Я-._~"),
        )
    }

    @Test
    fun `no feed item deeplink for an empty post id`() {
        assertNull(createFeedItemDeeplink(""))
    }

    @Test
    fun `intent data wins over the post id`() {
        assertEquals(
            "retrodrom://feed/item/7",
            extractDeeplink(data = "retrodrom://feed/item/7", postId = "1001"),
        )
    }

    @Test
    fun `deeplink is built from the post id without intent data`() {
        assertEquals(
            createFeedItemDeeplink("1001"),
            extractDeeplink(data = null, postId = "1001"),
        )
    }

    @Test
    fun `foreign scheme is not a deeplink`() {
        assertNull(extractDeeplink(data = "https://retrodrom.games/", postId = "1001"))
        assertNull(extractDeeplink(data = "retrodrom-feed", postId = null))
    }

    @Test
    fun `nothing to extract`() {
        assertNull(extractDeeplink(data = null, postId = null))
        assertNull(extractDeeplink(data = null, postId = ""))
    }
}
