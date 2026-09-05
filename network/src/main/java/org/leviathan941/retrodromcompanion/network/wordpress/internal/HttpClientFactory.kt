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

package org.leviathan941.retrodromcompanion.network.wordpress.internal

import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.resources.Resources
import io.ktor.client.request.url
import io.ktor.http.ContentType
import io.ktor.serialization.kotlinx.json.json
import io.ktor.serialization.kotlinx.xml.xml
import kotlinx.serialization.json.Json
import nl.adaptivity.xmlutil.serialization.XML
import org.leviathan941.retrodromcompanion.common.Constants
import javax.inject.Inject

internal const val FEED_PATH_SEGMENT = "feed"
internal const val FEED_PAGE_QUERY_PARAM = "paged"

private val RSS_CONTENT_TYPE = ContentType("application", "rss+xml")

internal class HttpClientFactory @Inject constructor(
    private val engine: HttpClientEngine,
) {
    fun create(): HttpClient = HttpClient(engine) {
        install(plugin = Resources)
        install(plugin = ContentNegotiation) {
            json(
                Json {
                    ignoreUnknownKeys = true
                },
            )
            val xmlFormat = XML {
                // The feed carries many elements and namespaces the app does not model.
                defaultPolicy {
                    ignoreUnknownChildren()
                }
            }
            xml(format = xmlFormat)
            // WordPress serves the feed as application/rss+xml, which the XML converter is not
            // registered for by default.
            xml(format = xmlFormat, contentType = RSS_CONTENT_TYPE)
        }
        defaultRequest {
            url(urlString = Constants.RETRODROM_BASE_URL)
        }
    }
}
