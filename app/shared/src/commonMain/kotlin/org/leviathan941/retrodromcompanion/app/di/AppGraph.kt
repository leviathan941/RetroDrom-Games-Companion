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

import dev.zacsweers.metrox.viewmodel.ViewModelGraph
import io.ktor.client.HttpClient
import org.leviathan941.retrodromcompanion.app.migration.AppDataMigrator
import org.leviathan941.retrodromcompanion.notification.Notifications
import org.leviathan941.retrodromcompanion.notification.PushMessaging
import org.leviathan941.retrodromcompanion.preferences.PreferencesRepository

/**
 * The single application-wide object graph, built once per process by [org.leviathan941.retrodromcompanion.app.SharedApplication]
 * and reached from the platform's entry points through it.
 *
 * Each platform declares the `@DependencyGraph` that implements it, since a graph must see the
 * platform-specific contributions.
 */
public interface AppGraph : ViewModelGraph {
    public val appDataMigrator: AppDataMigrator
    public val notifications: Notifications
    public val pushMessaging: PushMessaging
    public val preferencesRepository: PreferencesRepository

    // Deliberately a Provider: Coil only builds its ImageLoader on the first image request, so
    // the client must not be created at process start. The binding is app-scoped, so every
    // invocation returns the one shared client.
    public val httpClient: () -> HttpClient
}
