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

package org.leviathan941.retrodromcompanion.app.migration

/**
 * State of the one-shot app data migration run.
 *
 * Doubles as the runner's re-entrance guard: [AppDataMigrator.start] only starts a run when
 * it can move the state out of [Pending].
 */
public sealed interface AppDataMigrationState {
    /**
     * [AppDataMigrator.start] has not been called yet.
     */
    public data object Pending : AppDataMigrationState

    /**
     * Migrations are running. The UI must not read migrated app data yet.
     */
    public data object Running : AppDataMigrationState

    /**
     * The run is over. Reached on success and on a handled failure alike, so that a broken
     * migration can never keep the UI waiting forever.
     */
    public data object Finished : AppDataMigrationState
}
