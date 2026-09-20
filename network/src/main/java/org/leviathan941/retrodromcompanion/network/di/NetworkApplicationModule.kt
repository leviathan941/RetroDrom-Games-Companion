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

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.Binds
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.okhttp.OkHttp
import org.leviathan941.retrodromcompanion.network.wordpress.WpKtorClient
import org.leviathan941.retrodromcompanion.network.wordpress.WpNetworkClient

@BindingContainer
@ContributesTo(AppScope::class)
public abstract class NetworkApplicationModule {
    @Binds
    internal abstract val WpKtorClient.bindRetrodromWpRetrofitClient: WpNetworkClient

    internal companion object {
        @Provides
        @SingleIn(AppScope::class)
        internal fun provideHttpClientEngine(): HttpClientEngine = OkHttp.create()
    }
}
