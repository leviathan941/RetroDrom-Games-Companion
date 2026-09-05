/*
 * RetroDrom Games Companion
 * Copyright (C) 2025 Alexey Kuzin <amkuzink@gmail.com>.
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

import android.util.Log
import io.ktor.client.call.body
import io.ktor.client.plugins.resources.get
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.http.appendPathSegments
import io.ktor.http.isSuccess
import io.ktor.http.takeFrom
import kotlinx.coroutines.CancellationException
import org.leviathan941.retrodromcompanion.network.wordpress.internal.FEED_PAGE_QUERY_PARAM
import org.leviathan941.retrodromcompanion.network.wordpress.internal.FEED_PATH_SEGMENT
import org.leviathan941.retrodromcompanion.network.wordpress.internal.HttpClientFactory
import org.leviathan941.retrodromcompanion.network.wordpress.internal.WP_TAG
import org.leviathan941.retrodromcompanion.network.wordpress.internal.WpApiFeedCategories
import org.leviathan941.retrodromcompanion.network.wordpress.response.WpFeedCategory
import org.leviathan941.retrodromcompanion.network.wordpress.response.WpFeedChannel
import org.leviathan941.retrodromcompanion.network.wordpress.response.WpFeedRssResponse
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class WpKtorClient @Inject constructor(
    httpClientFactory: HttpClientFactory,
) : WpNetworkClient {
    private val httpClient = httpClientFactory.create()

    override suspend fun fetchCategories(): Result<List<WpFeedCategory>> = try {
        httpClient.get(WpApiFeedCategories()).handleResponse()
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        Log.e(WP_TAG, "fetchCategories: ${e.message}", e)
        Result.failure(
            WpGetErrorException(
                message = e.message ?: "Unknown error",
                cause = e,
            ),
        )
    }

    override suspend fun fetchRssFeedChannelPage(
        channelUrl: String,
        pageNumber: Int,
    ): Result<WpFeedChannel> = try {
        // The channel URL is a WordPress category permalink, so the feed lives underneath it.
        httpClient.get {
            url {
                takeFrom(channelUrl)
                appendPathSegments(FEED_PATH_SEGMENT)
                parameters.append(FEED_PAGE_QUERY_PARAM, pageNumber.toString())
            }
        }.handleResponse<WpFeedRssResponse>().mapCatching { response ->
            response.channel
                ?: throw WpGetErrorException(message = "RSS response does not contain channel")
        }
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        Log.e(WP_TAG, "fetchRssFeedChannelPage: ${e.message}", e)
        Result.failure(
            WpGetErrorException(
                message = e.message ?: "Unknown error",
                cause = e,
            ),
        )
    }

    private suspend inline fun <reified T> HttpResponse.handleResponse(): Result<T> {
        logResponse()
        return if (status.isSuccess()) {
            Result.success(body<T>())
        } else {
            Result.failure(
                WpGetErrorException(
                    message = "Code: ${status.value}, Message: ${status.description}",
                ),
            )
        }
    }

    private fun HttpResponse.logResponse() {
        Log.d(
            WP_TAG,
            """
                wpResponse:
                    isSuccessful=${status.isSuccess()},
                    code=${status.value},
                    message=${status.description}
            """.trimIndent(),
        )
    }
}
