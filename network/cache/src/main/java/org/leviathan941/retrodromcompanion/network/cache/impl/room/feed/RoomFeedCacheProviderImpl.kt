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

package org.leviathan941.retrodromcompanion.network.cache.impl.room.feed

import android.content.Context
import android.util.Log
import androidx.room.Room
import androidx.room.withTransaction
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import org.leviathan941.retrodromcompanion.common.di.DiKeys
import org.leviathan941.retrodromcompanion.network.cache.api.feed.FeedCacheProvider
import org.leviathan941.retrodromcompanion.network.cache.api.feed.FeedCategory
import org.leviathan941.retrodromcompanion.network.cache.internal.room.feed.RoomFeedDatabase
import org.leviathan941.retrodromcompanion.network.cache.internal.room.feed.category.RoomFeedCategoryEntity
import org.leviathan941.retrodromcompanion.network.wordpress.WpGetErrorException
import org.leviathan941.retrodromcompanion.network.wordpress.WpRetrofitClient
import org.leviathan941.retrodromcompanion.network.wordpress.response.WpFeedCategory
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

private const val FEED_CACHE_DATABASE_NAME: String = "feed_cache.db"

private const val TAG = "RoomFeedCacheProvider"

@Singleton
internal class RoomFeedCacheProviderImpl @Inject constructor(
    @param:ApplicationContext
    private val context: Context,
    @param:Named(DiKeys.RETRODROM_WP_RETROFIT_CLIENT)
    private val wpRetrofitClient: WpRetrofitClient,
    @param:Named(DiKeys.APPLICATION_COROUTINE_SCOPE)
    private val coroutineScope: CoroutineScope,
) : FeedCacheProvider {

    private val database: RoomFeedDatabase by lazy {
        Room.databaseBuilder(
            context = context,
            klass = RoomFeedDatabase::class.java,
            name = FEED_CACHE_DATABASE_NAME,
        ).build()
    }

    override val categories: Flow<List<FeedCategory>>
        get() = database.categoriesDao().allFlow()

    override suspend fun refresh(): Result<Unit> =
        withContext(coroutineScope.coroutineContext + Dispatchers.IO) {
            fetchCategories().fold(
                onSuccess = { categories ->
                    database.withTransaction {
                        with(database.categoriesDao()) {
                            clear()
                            addAll(categories)
                        }
                    }
                    Result.success(Unit)
                },
                onFailure = {
                    Result.failure(it)
                },
            )
        }

    private suspend fun fetchCategories(): Result<List<RoomFeedCategoryEntity>> = try {
        wpRetrofitClient.fetchCategories()
            .map { it.toEntity() }
            .let {
                Log.d(TAG, "Fetched ${it.size} categories from WP")
                Result.success(it)
            }
    } catch (e: WpGetErrorException) {
        Log.e(TAG, "Failed to fetch categories", e)
        Result.failure(e)
    }

    private fun WpFeedCategory.toEntity(): RoomFeedCategoryEntity = RoomFeedCategoryEntity(
        id = this.id,
        name = this.name,
        link = this.link,
        postsCount = this.postsCount,
    )
}
