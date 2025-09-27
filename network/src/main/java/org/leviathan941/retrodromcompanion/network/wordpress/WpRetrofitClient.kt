/*
 * RetroDrom Games Companion
 * Copyright (C) 2024 Alexey Kuzin <amkuzink@gmail.com>.
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.leviathan941.retrodromcompanion.network.wordpress.internal.WP_TAG
import org.leviathan941.retrodromcompanion.network.wordpress.internal.WpApiService
import org.leviathan941.retrodromcompanion.network.wordpress.response.WpFeedCategory
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

public class WpRetrofitClient(
    baseUrl: String,
) {
    private val retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val wpApiService: WpApiService by lazy { retrofit.create(WpApiService::class.java) }

    @Throws(WpGetErrorException::class)
    public suspend fun fetchCategories(): List<WpFeedCategory> = withContext(Dispatchers.IO) {
        @SuppressWarnings("TooGenericExceptionCaught")
        try {
            wpApiService.fetchCategories().handleResponse() ?: emptyList()
        } catch (e: Exception) {
            Log.e(WP_TAG, "fetchCategories: ${e.message}", e)
            throw WpGetErrorException(
                message = e.message ?: "Unknown error",
                cause = e,
            )
        }
    }

    private fun <T> Response<T>.handleResponse(): T? {
        logResponse()
        return if (isSuccessful) {
            body()
        } else {
            throw WpGetErrorException(
                message = "Code: ${code()}, Message: ${message()}",
            )
        }
    }

    private fun <T> Response<T>.logResponse() {
        Log.d(
            WP_TAG,
            """fetchCategories: {
            isSuccessful=$isSuccessful,
            code=${code()},
            message=${message()},
            errorBody=${errorBody()},
            body=${body()}
        }
            """.trimIndent(),
        )
    }
}
