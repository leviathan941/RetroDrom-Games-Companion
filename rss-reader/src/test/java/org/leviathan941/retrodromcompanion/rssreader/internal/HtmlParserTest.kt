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

package org.leviathan941.retrodromcompanion.rssreader.internal

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

/**
 * Pins the feed item's image to the first `<img src>` in its description, which
 * `android.text.Html`'s `ImageGetter` used to report.
 */
class HtmlParserTest {

    @Test
    fun `featured image of a feed description`() {
        val html = "<a href=\"https://retrodrom.games/news/post/\" rel=\"nofollow\">" +
            "<img width=\"1024\" height=\"577\" " +
            "src=\"https://retrodrom.games/wp-content/uploads/2026/10/" +
            "moto-x-switch-1024x577.jpg\" " +
            "class=\"webfeedsFeaturedVisual wp-post-image\" alt=\"\" " +
            "style=\"display: block; margin: auto;\" decoding=\"async\" loading=\"lazy\" /></a>" +
            "<p>Отменённая гоночная игра Moto-X получила цифровой релиз.</p>" +
            "<p><img src=\"https://retrodrom.games/wp-content/uploads/2026/10/inline.jpg\" /></p>"

        assertEquals(
            "https://retrodrom.games/wp-content/uploads/2026/10/moto-x-switch-1024x577.jpg",
            findFirstImageSource(html),
        )
    }

    @Test
    fun `entities in the source are decoded`() {
        assertEquals(
            "https://example.com/a.jpg?w=1&h=2",
            findFirstImageSource("<img src=\"https://example.com/a.jpg?w=1&amp;h=2\">"),
        )
    }

    @Test
    fun `tag and attribute names are case-insensitive`() {
        assertEquals(
            "https://example.com/a.jpg",
            findFirstImageSource("<P><IMG SRC=\"https://example.com/a.jpg\"></P>"),
        )
    }

    @Test
    fun `image without a source is skipped`() {
        assertEquals(
            "https://example.com/b.jpg",
            findFirstImageSource(
                "<img alt=\"none\"><img src=\" \"><img src=\"https://example.com/b.jpg\">",
            ),
        )
    }

    @Test
    fun `no image`() {
        assertNull(findFirstImageSource("<p>Text only</p>"))
        assertNull(findFirstImageSource(""))
    }
}
