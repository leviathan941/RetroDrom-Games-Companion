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
import dev.zacsweers.metro.createGraphFactory
import org.leviathan941.retrodromcompanion.app.di.AppGraph
import org.leviathan941.retrodromcompanion.firebase.push.MessagingDependencies
import org.leviathan941.retrodromcompanion.notification.Notifications

class MainApplication :
    Application(),
    SingletonImageLoader.Factory,
    MessagingDependencies {
    val appGraph: AppGraph by lazy { createGraphFactory<AppGraph.Factory>().create(this) }

    override val notifications: Notifications
        get() = appGraph.notifications

    override fun onCreate() {
        super.onCreate()
        // Call to initialize.
        notifications
        appGraph.appDataMigrator.start()
    }

    // Coil shares the app's base Ktor client rather than building its own, so image loading and
    // the WordPress client use one engine and one connection pool.
    override fun newImageLoader(context: PlatformContext): ImageLoader =
        ImageLoader.Builder(context)
            .components {
                add(
                    KtorNetworkFetcherFactory(
                        httpClient = { appGraph.httpClient() },
                    ),
                )
            }
            .build()
}
