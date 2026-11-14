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

import androidx.room3.Room
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn
import kotlinx.cinterop.ExperimentalForeignApi
import org.leviathan941.retrodromcompanion.network.cache.internal.room.feed.FEED_CACHE_DATABASE_NAME
import org.leviathan941.retrodromcompanion.network.cache.internal.room.feed.RoomFeedDatabase
import org.leviathan941.retrodromcompanion.network.cache.internal.room.feed.RoomFeedDatabaseConstructor
import org.leviathan941.retrodromcompanion.network.cache.internal.room.feed.buildFeedDatabase
import platform.Foundation.NSCachesDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask

@BindingContainer
@ContributesTo(AppScope::class)
public object RoomFeedPlatformModule {
    @Provides
    @SingleIn(AppScope::class)
    internal fun provideRoomFeedDatabase(): RoomFeedDatabase = Room.databaseBuilder(
        name = "${cachesDirectory()}/$FEED_CACHE_DATABASE_NAME",
        factory = RoomFeedDatabaseConstructor::initialize,
    ).buildFeedDatabase()
}

// The feed is refetched when missing, so it belongs in Caches: not backed up, and purged by the
// system only while the app is not running.
@OptIn(ExperimentalForeignApi::class)
private fun cachesDirectory(): String {
    val url = NSFileManager.defaultManager.URLForDirectory(
        directory = NSCachesDirectory,
        inDomain = NSUserDomainMask,
        appropriateForURL = null,
        create = true,
        error = null,
    )
    return requireNotNull(url?.path) { "No Caches directory" }
}
