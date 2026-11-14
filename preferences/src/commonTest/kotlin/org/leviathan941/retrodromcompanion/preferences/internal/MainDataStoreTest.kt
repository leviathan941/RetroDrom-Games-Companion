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

package org.leviathan941.retrodromcompanion.preferences.internal

import androidx.datastore.core.okio.OkioStorage
import androidx.datastore.preferences.core.PreferencesSerializer
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.preferencesOf
import kotlin.random.Random
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import okio.FileSystem
import org.leviathan941.retrodromcompanion.preferences.PreferencesRepository

class MainDataStoreTest {
    private val directory =
        FileSystem.SYSTEM_TEMPORARY_DIRECTORY / "main-data-store-${Random.nextLong()}"

    @AfterTest
    fun deleteDirectory() {
        FileSystem.SYSTEM.deleteRecursively(directory)
    }

    @Test
    fun storeCreatesItsDirectoryAndPersistsEdits() = runTest {
        val file = directory / "datastore" / "$MAIN_DATA_STORE_NAME$MAIN_DATA_STORE_FILE_EXTENSION"
        val storage = OkioStorage(
            fileSystem = FileSystem.SYSTEM,
            serializer = PreferencesSerializer,
            producePath = { file },
        )
        val repository = PreferencesRepository(buildMainDataStore(storage))

        repository.uiEditor.setAppTheme("dark")
        repository.uiEditor.addPushTopicSubscription("new_retrodrom_posts")
        repository.promoEditor.decrementPushPostsPromoStartsUntilShow()

        assertEquals("dark", repository.ui.first().appTheme)
        assertEquals(setOf("new_retrodrom_posts"), repository.ui.first().subscribedPushTopics)
        assertEquals(
            PUSH_POSTS_PROMO_STARTS_UNTIL_SHOW_DEFAULT - 1,
            repository.promo.first().pushPosts.startsUntilShow,
        )
        assertTrue(FileSystem.SYSTEM.exists(file))
    }

    @Test
    fun migrationReplacesNewsPushFlagWithTopic() = runTest {
        val legacyKey = booleanPreferencesKey("news_push_enabled")
        val migration = Preference0To1Migration()
        val legacy = preferencesOf(legacyKey to true)

        assertTrue(migration.shouldMigrate(legacy))
        val migrated = migration.migrate(legacy)

        assertFalse(legacyKey in migrated)
        assertEquals(setOf("new_retrodrom_posts"), migrated[SUBSCRIBED_PUSH_TOPICS])
        assertFalse(migration.shouldMigrate(migrated))
    }
}
