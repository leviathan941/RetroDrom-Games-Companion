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

package org.leviathan941.retrodromcompanion.app.migration.internal

/**
 * A single step of the on-device app data migration.
 *
 * Steps are contributed to the runner as a multibound set, so an implementation may inject
 * whatever it needs (a [android.content.Context], a repository) instead of being handed a
 * fixed set of arguments.
 *
 * Implementations must be safe to run on a fresh install: a brand-new install starts at
 * version 0 just like an install that predates the mechanism, so every step runs there too.
 */
internal interface AppDataMigration {
    /**
     * 1-based, strictly increasing across the whole set. Never renumber or reuse a version,
     * and never unbind a step — a very old install would silently skip it.
     */
    val version: Int

    suspend fun migrate()
}
