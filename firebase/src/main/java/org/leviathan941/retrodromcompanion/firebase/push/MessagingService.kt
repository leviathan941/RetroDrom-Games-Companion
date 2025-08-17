/*
 * RetroDrom Games Companion
 * Copyright (C) 2024 Alexey Kuzin <amkuzink@gmail.com>.
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

package org.leviathan941.retrodromcompanion.firebase.push

import android.util.Log
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.leviathan941.retrodromcompanion.notification.NotificationData
import org.leviathan941.retrodromcompanion.notification.Notifications
import org.leviathan941.retrodromcompanion.preferences.Preferences.mainDataStore
import org.leviathan941.retrodromcompanion.preferences.PreferencesRepository

private const val TAG = "MessagingService"

@AndroidEntryPoint
internal class MessagingService : FirebaseMessagingService() {
    @Inject lateinit var notifications: Notifications

    override fun onNewToken(token: String) {
        Log.d(TAG, "Refreshed token: $token")
        ProcessLifecycleOwner.get().lifecycleScope.launch {
            PreferencesRepository(applicationContext.mainDataStore).ui.first().let { prefs ->
                prefs.subscribedPushTopics.mapNotNull {
                    Messaging.topicFromValue(it)
                }.forEach {
                    Messaging.subscribeToTopic(it)
                }
            }
        }
    }

    override fun onMessageReceived(message: RemoteMessage) {
        Log.d(TAG, "Received message from: ${message.from}")

        if (message.data.isNotEmpty()) {
            Log.d(TAG, "Message data payload: ${message.data}")
        }

        message.toNotificationData()?.let {
            Log.d(TAG, "Message notification data: $it")
            notifications.sendPushNotification(
                context = this,
                data = it,
            )
        }
    }

    private fun RemoteMessage.toNotificationData(): NotificationData? {
        val notification = notification ?: return null
        return notification.body?.let { body ->
            NotificationData(
                message = body,
                title = notification.title,
                channelId = notification.channelId,
                payloadData = data,
            )
        }
    }
}
