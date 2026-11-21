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

import android.content.Context
import dev.zacsweers.metro.Inject
import org.leviathan941.retrodromcompanion.common.di.ApplicationContext

private const val LEGACY_RSS_CACHE_DATABASE_NAME = "ktrssreader.db"

/**
 * Removes the database left behind by the KtRssReader library, which the app no longer opens.
 */
@Inject
internal class DeleteKtRssReaderCacheMigration(
    @param:ApplicationContext
    private val context: Context,
) : AppDataMigration {
    override val version: Int = 1

    override suspend fun migrate() {
        // Also removes the -wal/-shm/-journal siblings, and returns false harmlessly when the
        // database is absent, as it is on a fresh install.
        context.deleteDatabase(LEGACY_RSS_CACHE_DATABASE_NAME)
    }
}
