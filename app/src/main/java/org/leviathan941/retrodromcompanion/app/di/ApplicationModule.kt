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
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.lifecycle.lifecycleScope
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import org.leviathan941.retrodromcompanion.MainActivity
import org.leviathan941.retrodromcompanion.common.di.DiKeys
import org.leviathan941.retrodromcompanion.preferences.Preferences.mainDataStore
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface ApplicationModule {
    companion object {

        @Provides
        @Named(DiKeys.APPLICATION_COROUTINE_SCOPE)
        fun provideApplicationCoroutineScope(): CoroutineScope {
            return ProcessLifecycleOwner.get().lifecycleScope
        }

        @Provides
        @Named(DiKeys.MAIN_DATASTORE)
        @Singleton
        fun provideMainDataStore(
            application: Application
        ): DataStore<Preferences> {
            return application.applicationContext.mainDataStore
        }

        @Provides
        @Named(DiKeys.MAIN_ACTIVITY_CLASS)
        fun provideMainActivityClass(): Class<*> {
            return MainActivity::class.java
        }
    }
}