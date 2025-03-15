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

package org.leviathan941.retrodromcompanion.notification

import android.Manifest
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import org.leviathan941.retrodromcompanion.common.RequestCode
import org.leviathan941.retrodromcompanion.common.di.DiKeys
import org.leviathan941.retrodromcompanion.notification.internal.channelName
import org.leviathan941.retrodromcompanion.notification.internal.notificationChannelId
import org.leviathan941.retrodromcompanion.notification.internal.notificationId
import org.leviathan941.retrodromcompanion.notification.internal.visibility
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
public class Notifications @Inject constructor(
    @ApplicationContext
    private val context: Context,
    @Named(DiKeys.MAIN_ACTIVITY_CLASS)
    private val pushActivityClass: Class<*>,
) {

    init {
        NotificationManagerCompat.from(context)
            .createNotificationChannelsCompat(
                listOf(
                    newsChannel(context),
                    defaultChannel(context),
                )
            )
    }

    public fun sendPushNotification(
        context: Context,
        data: NotificationData,
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED) {
            return
        }

        val intent = Intent(context, pushActivityClass).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            setData(data.deeplink)
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            RequestCode.PUSH_NOTIFICATION,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val channelId = notificationChannelId(data.channelId)
        val title = data.title ?: channelId.channelName(context)
        val smallIcon = R.drawable.google_material_news
        val visibility = channelId.visibility()
        val notificationId = channelId.notificationId()
        NotificationCompat.Builder(context, channelId.value)
            .setContentTitle(title)
            .setContentText(data.message)
            .setContentIntent(pendingIntent)
            .setCategory(NotificationCompat.CATEGORY_SOCIAL)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setSmallIcon(smallIcon)
            .setAutoCancel(true)
            .setVisibility(visibility)
            .build()
            .let {
                NotificationManagerCompat.from(context).notify(notificationId, it)
            }
    }

    private fun newsChannel(context: Context): NotificationChannelCompat =
        createChannelWithDefaultConfig(
            channelId = NotificationChannelId.RETRODROM_RSS_POSTS,
            name = context.getString(R.string.rss_news_notification_channel_name),
            description = context.getString(R.string.rss_news_notification_channel_description),
        )

    private fun defaultChannel(context: Context): NotificationChannelCompat =
        createChannelWithDefaultConfig(
            channelId = NotificationChannelId.MISC,
            name = context.getString(R.string.misc_notification_channel_name),
            description = context.getString(R.string.misc_notification_channel_description),
        )

    private fun createChannelWithDefaultConfig(
        channelId: NotificationChannelId,
        name: String,
        description: String,
    ): NotificationChannelCompat =
        NotificationChannelCompat.Builder(
            channelId.value,
            NotificationManagerCompat.IMPORTANCE_DEFAULT
        ).setDescription(description)
            .setName(name)
            .setShowBadge(true)
            .setVibrationEnabled(true)
            .build()
}