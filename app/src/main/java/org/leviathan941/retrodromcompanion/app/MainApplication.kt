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

package org.leviathan941.retrodromcompanion.app

import android.app.Application
import coil3.ImageLoader
import coil3.PlatformContext
import coil3.SingletonImageLoader
import coil3.network.ktor3.KtorNetworkFetcherFactory
import dagger.Lazy
import dagger.hilt.android.HiltAndroidApp
import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import javax.inject.Inject
import org.leviathan941.retrodromcompanion.app.migration.AppDataMigrator
import org.leviathan941.retrodromcompanion.notification.Notifications

@HiltAndroidApp
class MainApplication :
    Application(),
    SingletonImageLoader.Factory {
    @Inject lateinit var notifications: Notifications

    @Inject lateinit var appDataMigrator: AppDataMigrator

    @Inject lateinit var httpClientEngine: Lazy<HttpClientEngine>

    override fun onCreate() {
        super.onCreate()
        // Call to initialize
        notifications
        appDataMigrator.start()
    }

    // Coil shares the app's Ktor engine rather than discovering its own through ServiceLoader,
    // so image loading and the WordPress client use one connection pool.
    override fun newImageLoader(context: PlatformContext): ImageLoader =
        ImageLoader.Builder(context)
            .components {
                add(KtorNetworkFetcherFactory(httpClient = { HttpClient(httpClientEngine.get()) }))
            }
            .build()
}
