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

package org.leviathan941.retrodromcompanion.app.di

import android.app.Application
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.DependencyGraph
import dev.zacsweers.metro.Provides
import dev.zacsweers.metrox.viewmodel.ViewModelGraph
import io.ktor.client.HttpClient
import org.leviathan941.retrodromcompanion.app.migration.AppDataMigrator
import org.leviathan941.retrodromcompanion.firebase.push.MessagingDependencies

/**
 * The single application-wide object graph. Built once in `MainApplication.onCreate` and reached
 * from framework-instantiated classes through the `Application`, since neither an Activity nor a
 * Service can be constructor injected.
 */
@DependencyGraph(AppScope::class)
interface AppGraph :
    ViewModelGraph,
    MessagingDependencies {

    val appDataMigrator: AppDataMigrator

    // Deliberately a Provider: Coil only builds its ImageLoader on the first image request, so
    // the client must not be created at process start. The binding is app-scoped, so every
    // invocation returns the one shared client.
    val httpClient: () -> HttpClient

    @DependencyGraph.Factory
    fun interface Factory {
        fun create(@Provides application: Application): AppGraph
    }
}
