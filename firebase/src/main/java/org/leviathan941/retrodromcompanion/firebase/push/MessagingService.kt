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
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import org.leviathan941.retrodromcompanion.notification.NotificationData
import org.leviathan941.retrodromcompanion.notification.Notifications

private const val TAG = "MessagingService"

internal class MessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        Log.d(TAG, "Refreshed token: $token")
        // TODO: Re-subscribe to all subscribed topics
    }

    override fun onMessageReceived(message: RemoteMessage) {
        Log.d(TAG, "Received message from: ${message.from}")

        if (message.data.isNotEmpty()) {
            Log.d(TAG, "Message data payload: ${message.data}")
        }

        message.notification?.toNotificationData()?.let {
            Log.d(TAG, "Message notification data: $it")
            Notifications.sendPushNotification(
                context = this,
                data = it,
            )
        }
    }

    private fun RemoteMessage.Notification.toNotificationData(): NotificationData? {
        return body?.let { body ->
            NotificationData(
                message = body,
                title = title,
                deeplink = link,
                channelId = channelId,
            )
        }
    }
}
