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

package org.leviathan941.retrodromcompanion.common.di

import dev.zacsweers.metro.Qualifier

/**
 * The application [android.content.Context].
 */
@Qualifier
public annotation class ApplicationContext

/**
 * The process-lifetime `CoroutineScope`, bound to the `ProcessLifecycleOwner`.
 * Work launched in it outlives any Activity but dies with the process.
 */
@Qualifier
public annotation class ApplicationCoroutineScope

/**
 * The app's single `DataStore<Preferences>`, backing every stored preference.
 */
@Qualifier
public annotation class MainDataStore

/**
 * The [Class] of the Activity a push notification opens.
 */
@Qualifier
public annotation class MainActivityClass
