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

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Multibinds
import org.leviathan941.retrodromcompanion.app.migration.internal.AppDataMigration

/**
 * The migration registry. No step has shipped on iOS yet, so the set is declared allowed to be
 * empty. Bind each new step here with `@Binds @IntoSet`, and never unbind one: the runner picks
 * the ones newer than the stored version, so unbinding a step would let an old installation
 * skip it.
 */
@BindingContainer
@ContributesTo(AppScope::class)
public abstract class AppDataMigrationApplicationModule {
    @Multibinds(allowEmpty = true)
    internal abstract val appDataMigrations: Set<AppDataMigration>
}
