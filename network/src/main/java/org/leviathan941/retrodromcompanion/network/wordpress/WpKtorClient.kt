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
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.resources.Resources
import io.ktor.client.plugins.resources.get
import io.ktor.client.statement.HttpResponse
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.leviathan941.retrodromcompanion.common.Constants
import org.leviathan941.retrodromcompanion.network.wordpress.internal.WP_TAG
import org.leviathan941.retrodromcompanion.network.wordpress.internal.WpApiCategories
import org.leviathan941.retrodromcompanion.network.wordpress.response.WpFeedCategory
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class WpKtorClient @Inject constructor() : WpNetworkClient {
    private val httpClient = HttpClient(engineFactory = OkHttp) {
        install(plugin = Resources)
        install(plugin = ContentNegotiation) {
            json(
                Json {
                    ignoreUnknownKeys = true
                },
            )
        }
        defaultRequest {
            url(urlString = Constants.RETRODROM_BASE_URL)
        }
    }

    override suspend fun fetchCategories(): List<WpFeedCategory> = try {
        httpClient.get(WpApiCategories()).handleResponse() ?: emptyList()
    } catch (e: Exception) {
        Log.e(WP_TAG, "fetchCategories: ${e.message}", e)
        throw WpGetErrorException(
            message = e.message ?: "Unknown error",
            cause = e,
        )
    }

    private suspend inline fun <reified T> HttpResponse.handleResponse(): T? {
        logResponse()
        return if (status.isSuccess()) {
            body<T>()
        } else {
            throw WpGetErrorException(
                message = "Code: ${status.value}, Message: ${status.description}",
            )
        }
    }

    private fun HttpResponse.logResponse() {
        Log.d(
            WP_TAG,
            """
                fetchCategories:
                    isSuccessful=${status.isSuccess()},
                    code=${status.value},
                    message=${status.description}
            """.trimIndent(),
        )
    }
}
