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
import org.leviathan941.retrodromcompanion.app.licenses.AppLibrariesSource
import org.leviathan941.retrodromcompanion.common.di.AppVersionName
import org.leviathan941.retrodromcompanion.common.di.MainActivityClass

@DependencyGraph(AppScope::class)
internal interface AndroidAppGraph : AppGraph {

    /**
     * Takes what only the application module can see: its identity and its generated resources.
     */
    @DependencyGraph.Factory
    fun interface Factory {
        fun create(
            @Provides application: Application,
            @Provides @AppVersionName appVersionName: String,
            @Provides @MainActivityClass mainActivityClass: Class<*>,
            @Provides appLibrariesSource: AppLibrariesSource,
        ): AndroidAppGraph
    }
}
