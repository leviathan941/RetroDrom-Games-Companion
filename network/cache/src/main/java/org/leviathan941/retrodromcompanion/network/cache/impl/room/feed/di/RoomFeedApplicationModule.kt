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

package org.leviathan941.retrodromcompanion.network.cache.impl.room.feed.di

import android.content.Context
import androidx.room.Room
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import org.leviathan941.retrodromcompanion.network.cache.api.feed.FeedCacheMutator
import org.leviathan941.retrodromcompanion.network.cache.api.feed.FeedCacheProvider
import org.leviathan941.retrodromcompanion.network.cache.impl.room.feed.RoomFeedCacheMutatorImpl
import org.leviathan941.retrodromcompanion.network.cache.impl.room.feed.RoomFeedCacheProviderImpl
import org.leviathan941.retrodromcompanion.network.cache.internal.room.feed.FEED_CACHE_DATABASE_NAME
import org.leviathan941.retrodromcompanion.network.cache.internal.room.feed.RoomFeedDatabase
import javax.inject.Singleton

@Suppress("unused")
@Module
@InstallIn(SingletonComponent::class)
public abstract class RoomFeedApplicationModule {
    @Binds
    internal abstract fun bindFeedCacheProvider(impl: RoomFeedCacheProviderImpl): FeedCacheProvider

    @Binds
    internal abstract fun bindFeedCacheMutator(impl: RoomFeedCacheMutatorImpl): FeedCacheMutator

    internal companion object {
        // Each instance owns its own invalidation tracker, so a second one would not see the
        // writes made through the first.
        @Provides
        @Singleton
        fun provideRoomFeedDatabase(
            @ApplicationContext
            context: Context,
        ): RoomFeedDatabase = Room.databaseBuilder(
            context = context,
            klass = RoomFeedDatabase::class.java,
            name = FEED_CACHE_DATABASE_NAME,
        ).build()
    }
}
