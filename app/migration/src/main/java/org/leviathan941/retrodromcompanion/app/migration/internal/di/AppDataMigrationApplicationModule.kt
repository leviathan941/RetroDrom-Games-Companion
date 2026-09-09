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

package org.leviathan941.retrodromcompanion.app.migration.internal.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import org.leviathan941.retrodromcompanion.app.migration.internal.AppDataMigration
import org.leviathan941.retrodromcompanion.app.migration.internal.DeleteKtRssReaderCacheMigration

/**
 * The migration registry. Every step ever shipped stays bound here: the runner picks the ones
 * newer than the stored version and applies them in version order, so unbinding a step would
 * let a sufficiently old installation skip it.
 */
@Suppress("unused")
@Module
@InstallIn(SingletonComponent::class)
internal abstract class AppDataMigrationApplicationModule {
    @Binds
    @IntoSet
    abstract fun bindDeleteKtRssReaderCacheMigration(
        impl: DeleteKtRssReaderCacheMigration,
    ): AppDataMigration
}
