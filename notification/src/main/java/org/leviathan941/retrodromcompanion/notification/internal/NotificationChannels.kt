/*
 * RetroDrom Games Companion
 * Copyright (C) 2025 Alexey Kuzin <amkuzink@gmail.com>.
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

package org.leviathan941.retrodromcompanion.notification.internal

import android.content.Context
import android.net.Uri
import org.leviathan941.retrodromcompanion.notification.NotificationChannelId
import org.leviathan941.retrodromcompanion.notification.POST_ID_PAYLOAD_DATA_KEY
import org.leviathan941.retrodromcompanion.notification.R
import org.leviathan941.retrodromcompanion.notification.createFeedItemDeeplink
import java.util.UUID
import kotlin.math.absoluteValue

// Channel IDs:
internal const val NEW_POSTS_CHANNEL_ID = "retrodrom_rss_posts"
// It must be the same as "push_notification_default_channel_id"
internal const val MISC_CHANNEL_ID = "misc"

internal fun notificationChannelId(
    value: String?
): NotificationChannelId =
    NotificationChannelId.entries.find { it.value == value }
        ?: NotificationChannelId.MISC

internal fun NotificationChannelId.channelName(
    context: Context
): String =
    when (this) {
        NotificationChannelId.RETRODROM_RSS_POSTS ->
            context.getString(R.string.rss_news_notification_channel_name)
        NotificationChannelId.MISC ->
            context.getString(R.string.misc_notification_channel_name)
    }

internal fun NotificationChannelId.visibility(): Int =
    when (this) {
        NotificationChannelId.RETRODROM_RSS_POSTS ->
            android.app.Notification.VISIBILITY_PUBLIC
        NotificationChannelId.MISC ->
            android.app.Notification.VISIBILITY_PRIVATE
    }

internal fun NotificationChannelId.notificationId(): Int =
    when (this) {
        NotificationChannelId.RETRODROM_RSS_POSTS,
        NotificationChannelId.MISC ->
            uniqueNotificationId()
    }

private fun uniqueNotificationId(): Int =
    UUID.randomUUID().hashCode().absoluteValue

internal fun NotificationChannelId.deeplink(data: Map<String, String>): Uri? {
    return when (this) {
        NotificationChannelId.RETRODROM_RSS_POSTS ->
            data[POST_ID_PAYLOAD_DATA_KEY]?.let { createFeedItemDeeplink(it) }
        NotificationChannelId.MISC ->
            null
    }
}
