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

package org.leviathan941.retrodromcompanion.app

import coil3.ImageLoader
import coil3.SingletonImageLoader
import coil3.network.ktor3.KtorNetworkFetcherFactory
import org.leviathan941.retrodromcompanion.app.di.AppGraph

/**
 * The process-wide part of the app that every platform shares. The platform's application
 * object builds it once, holds it, and calls [onCreate] when the process starts.
 */
public class SharedApplication internal constructor(
    public val appGraph: AppGraph,
) {
    public fun onCreate() {
        // Call to initialize.
        appGraph.notifications
        appGraph.appDataMigrator.start()
        // Coil shares the app's base Ktor client rather than building its own, so image loading
        // and the WordPress client use one engine and one connection pool.
        SingletonImageLoader.setSafe { context ->
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
    }
}
