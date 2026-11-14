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

package org.leviathan941.retrodromcompanion.network.wordpress.response

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import nl.adaptivity.xmlutil.serialization.XML
import org.leviathan941.retrodromcompanion.network.wordpress.WP_FEED_SAMPLE

/**
 * A WordPress feed carries plenty of elements and namespaces that are not modelled here, so a
 * change to the serialization policy can silently stop the modelled ones from being picked up.
 */
class WpFeedRssResponseTest {
    private val xml = XML {
        defaultPolicy {
            ignoreUnknownChildren()
        }
    }

    private fun parseChannel(): WpFeedChannel =
        requireNotNull(xml.decodeFromString(WpFeedRssResponse.serializer(), WP_FEED_SAMPLE).channel)

    @Test
    fun `parses channel of a WordPress RSS feed`() {
        val channel = parseChannel()

        assertEquals("Sample Feed", channel.title)
        assertEquals("https://example.com/", channel.link)
        assertEquals(2, channel.items.size)
    }

    @Test
    fun `parses every modelled item field`() {
        val item = parseChannel().items.first()

        assertEquals("Первая запись с кириллицей", item.title)
        assertEquals("https://example.com/review/first-post/", item.link)
        assertEquals("Fri, 04 Sep 2026 04:45:38 +0000", item.pubDate)
        assertEquals(listOf("Обзоры"), item.categories)
        assertEquals("Первый Автор", item.creator)
        assertEquals("1001", item.postId)
        assertTrue(
            item.description.orEmpty().startsWith("<a href="),
            "Description must keep its HTML markup: ${item.description}",
        )
    }

    @Test
    fun `parses repeated category elements`() {
        val item = parseChannel().items.last()

        assertEquals("1002", item.postId)
        assertEquals(listOf("Новости", "Конкурсы"), item.categories)
    }
}
