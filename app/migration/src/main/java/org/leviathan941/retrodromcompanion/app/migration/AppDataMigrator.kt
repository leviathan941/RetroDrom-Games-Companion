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

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import org.leviathan941.retrodromcompanion.app.migration.internal.AppDataMigration
import org.leviathan941.retrodromcompanion.common.di.ApplicationCoroutineScope
import org.leviathan941.retrodromcompanion.common.logging.Logger
import org.leviathan941.retrodromcompanion.preferences.PreferencesRepository
import kotlin.time.Duration.Companion.milliseconds

private val logger = Logger.withTag("AppDataMigrator")
private const val MIGRATION_TIMEOUT_MS = 10_000L
private const val NO_MIGRATIONS_VERSION = 0

/**
 * Applies the versioned on-device app data migrations once per process.
 *
 * The run happens off the main thread, so it must not be awaited by blocking. Observe [state]
 * instead and keep the UI from reading app data until it turns
 * [AppDataMigrationState.Finished].
 */
@Inject
@SingleIn(AppScope::class)
public class AppDataMigrator internal constructor(
    @param:ApplicationCoroutineScope
    private val scope: CoroutineScope,
    private val migrations: Set<AppDataMigration>,
    private val preferencesRepository: PreferencesRepository,
) {
    public val state: StateFlow<AppDataMigrationState>
        field = MutableStateFlow<AppDataMigrationState>(AppDataMigrationState.Pending)

    /**
     * Starts the run. Subsequent calls are ignored.
     */
    public fun start() {
        val started = state.compareAndSet(
            expect = AppDataMigrationState.Pending,
            update = AppDataMigrationState.Running,
        )
        if (!started) {
            return
        }
        scope.launch(Dispatchers.IO) {
            try {
                withTimeout(MIGRATION_TIMEOUT_MS.milliseconds) {
                    migrate()
                }
            } catch (e: TimeoutCancellationException) {
                logger.e(e) { "App data migration timed out" }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                logger.e(e) { "App data migration failed" }
            } finally {
                state.value = AppDataMigrationState.Finished
            }
        }
    }

    private suspend fun migrate() {
        val currentVersion = migrations.maxOfOrNull { it.version } ?: NO_MIGRATIONS_VERSION
        val storedVersion = preferencesRepository.appData.first().version
        if (storedVersion >= currentVersion) {
            logger.d { "App data version $storedVersion is up to date" }
            return
        }
        logger.d { "Migrating app data from version $storedVersion to $currentVersion" }
        migrations
            .filter { it.version > storedVersion }
            .sortedBy { it.version }
            .forEach { migration ->
                migration.migrate()
                // Persisted after each step so that an interrupted run resumes at the right
                // place instead of replaying or skipping a migration.
                preferencesRepository.appDataEditor.setVersion(migration.version)
                logger.d { "Applied app data migration ${migration.version}" }
            }
    }
}
