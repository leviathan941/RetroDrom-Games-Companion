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

package org.leviathan941.retrodromcompanion.app.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import org.leviathan941.retrodromcompanion.common.di.ApplicationCoroutineScope
import org.leviathan941.retrodromcompanion.common.di.MainDataStore
import org.leviathan941.retrodromcompanion.common.logging.Logger
import org.leviathan941.retrodromcompanion.notification.NotificationData
import org.leviathan941.retrodromcompanion.notification.Notifications
import org.leviathan941.retrodromcompanion.notification.PushMessaging
import org.leviathan941.retrodromcompanion.preferences.createMainDataStore

private val logger = Logger.withTag("IosApplicationModule")

@BindingContainer
@ContributesTo(AppScope::class)
internal object ApplicationModule {

    // An iOS process is the app's lifetime, so the scope is never cancelled, like Android's
    // process lifecycle scope. Both run on the main thread.
    @Provides
    @ApplicationCoroutineScope
    @SingleIn(AppScope::class)
    fun provideApplicationCoroutineScope(): CoroutineScope = MainScope()

    @Provides
    @MainDataStore
    @SingleIn(AppScope::class)
    fun provideMainDataStore(): DataStore<Preferences> = createMainDataStore()

    // shortcut: shows nothing; C2 replaces it with local notifications.
    @Provides
    fun provideNotifications(): Notifications = object : Notifications {
        override fun show(data: NotificationData) {
            logger.w { "Notifications are not implemented on iOS, dropped one" }
        }
    }

    // shortcut: every subscription fails; C3 replaces it with APNs push.
    @Provides
    fun providePushMessaging(): PushMessaging = object : PushMessaging {
        override suspend fun subscribeToTopic(topic: PushMessaging.Topic): Boolean {
            logger.w { "Push messaging is not implemented on iOS, not subscribed to $topic" }
            return false
        }

        override suspend fun unsubscribeFromTopic(topic: PushMessaging.Topic): Boolean {
            logger.w { "Push messaging is not implemented on iOS, not unsubscribed from $topic" }
            return false
        }
    }
}
