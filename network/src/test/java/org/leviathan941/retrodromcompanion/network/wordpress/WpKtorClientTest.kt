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

package org.leviathan941.retrodromcompanion.network.wordpress

import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.MockRequestHandleScope
import io.ktor.client.engine.mock.respond
import io.ktor.client.engine.mock.respondError
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.Url
import io.ktor.http.headersOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.leviathan941.retrodromcompanion.network.wordpress.internal.WpHttpClientFactory

/**
 * Covers what the response models alone cannot show: how a feed URL is built on top of a channel
 * permalink, and that the `application/rss+xml` content type is negotiated.
 */
class WpKtorClientTest {
    private val sample: String = requireNotNull(
        javaClass.classLoader?.getResourceAsStream("wp_feed_sample.xml"),
    ).use { it.readBytes().decodeToString() }

    @Test
    fun `requests the paged feed underneath the channel URL`() = runTest {
        var requestedUrl: Url? = null
        val client = clientOf(
            MockEngine { request ->
                requestedUrl = request.url
                respondRss()
            },
        )

        val channel = client.fetchRssFeedChannelPage(
            channelUrl = "https://example.com/category/news/",
            pageNumber = 2,
        ).getOrThrow()

        val url = requireNotNull(requestedUrl)
        assertEquals("example.com", url.host)
        assertEquals("/category/news/feed", url.encodedPath)
        assertEquals("2", url.parameters["paged"])
        assertEquals(2, channel.items.size)
    }

    @Test
    fun `keeps the base URL as its own channel`() = runTest {
        var requestedUrl: Url? = null
        val client = clientOf(
            MockEngine { request ->
                requestedUrl = request.url
                respondRss()
            },
        )

        client.fetchRssFeedChannelPage(
            channelUrl = "https://example.com/",
            pageNumber = 1,
        ).getOrThrow()

        assertEquals("/feed", requireNotNull(requestedUrl).encodedPath)
    }

    @Test
    fun `returns a failure for an unsuccessful response`() = runTest {
        val client = clientOf(
            MockEngine { respondError(HttpStatusCode.NotFound) },
        )

        val result = client.fetchRssFeedChannelPage(
            channelUrl = "https://example.com/",
            pageNumber = 1,
        )

        assertTrue(result.exceptionOrNull() is WpGetErrorException)
    }

    private fun clientOf(engine: MockEngine): WpKtorClient =
        WpKtorClient(WpHttpClientFactory(HttpClient(engine)))

    private fun MockRequestHandleScope.respondRss() = respond(
        content = sample,
        headers = headersOf(HttpHeaders.ContentType, "application/rss+xml; charset=UTF-8"),
    )
}
