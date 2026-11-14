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

package org.leviathan941.retrodromcompanion.network.cache.impl.room.feed.di

import android.content.Context
import androidx.room3.Room
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn
import org.leviathan941.retrodromcompanion.common.di.ApplicationContext
import org.leviathan941.retrodromcompanion.network.cache.internal.room.feed.FEED_CACHE_DATABASE_NAME
import org.leviathan941.retrodromcompanion.network.cache.internal.room.feed.RoomFeedDatabase
import org.leviathan941.retrodromcompanion.network.cache.internal.room.feed.RoomFeedDatabaseConstructor
import org.leviathan941.retrodromcompanion.network.cache.internal.room.feed.buildFeedDatabase

@BindingContainer
@ContributesTo(AppScope::class)
public object RoomFeedPlatformModule {
    // A bare name resolves to the app's databases directory, where the file has always been.
    @Provides
    @SingleIn(AppScope::class)
    internal fun provideRoomFeedDatabase(
        @ApplicationContext
        context: Context,
    ): RoomFeedDatabase = Room.databaseBuilder(
        context = context,
        name = FEED_CACHE_DATABASE_NAME,
        factory = RoomFeedDatabaseConstructor::initialize,
    ).buildFeedDatabase()
}
