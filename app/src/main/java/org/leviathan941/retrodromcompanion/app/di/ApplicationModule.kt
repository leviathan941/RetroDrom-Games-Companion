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

package org.leviathan941.retrodromcompanion.app.di

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.lifecycle.lifecycleScope
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.CoroutineScope
import org.leviathan941.retrodromcompanion.MainActivity
import org.leviathan941.retrodromcompanion.common.di.ApplicationContext
import org.leviathan941.retrodromcompanion.common.di.ApplicationCoroutineScope
import org.leviathan941.retrodromcompanion.common.di.MainActivityClass
import org.leviathan941.retrodromcompanion.common.di.MainDataStore
import org.leviathan941.retrodromcompanion.preferences.Preferences.mainDataStore

@BindingContainer
@ContributesTo(AppScope::class)
object ApplicationModule {

    @Provides
    @ApplicationContext
    fun provideApplicationContext(application: Application): Context =
        application.applicationContext

    @Provides
    @ApplicationCoroutineScope
    fun provideApplicationCoroutineScope(): CoroutineScope =
        ProcessLifecycleOwner.get().lifecycleScope

    @Provides
    @MainDataStore
    @SingleIn(AppScope::class)
    fun provideMainDataStore(application: Application): DataStore<Preferences> =
        application.applicationContext.mainDataStore

    @Provides
    @MainActivityClass
    fun provideMainActivityClass(): Class<*> = MainActivity::class.java
}
