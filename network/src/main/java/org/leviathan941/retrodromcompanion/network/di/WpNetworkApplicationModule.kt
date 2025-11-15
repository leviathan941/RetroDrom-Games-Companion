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

package org.leviathan941.retrodromcompanion.network.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import org.leviathan941.retrodromcompanion.common.Constants
import org.leviathan941.retrodromcompanion.network.wordpress.WpNetworkClient
import org.leviathan941.retrodromcompanion.network.wordpress.WpRetrofitClient
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
public abstract class WpNetworkApplicationModule {
    public companion object {
        @Provides
        @Singleton
        public fun provideRetrodromWpRetrofitClient(): WpNetworkClient =
            WpRetrofitClient(Constants.RETRODROM_BASE_URL)
    }
}
