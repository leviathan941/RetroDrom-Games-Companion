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

package org.leviathan941.retrodromcompanion.app

import android.content.Context
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationManagerCompat
import org.leviathan941.retrodromcompanion.R

object Notifications {
    private const val NEW_POSTS_CHANNEL_ID = "retrodrom_rss_posts"

    fun createNotificationChannels(context: Context) {
        NotificationManagerCompat.from(context).createNotificationChannel(
            newsChannel(context)
        )
    }

    private fun newsChannel(context: Context): NotificationChannelCompat =
        NotificationChannelCompat.Builder(
            NEW_POSTS_CHANNEL_ID,
            NotificationManagerCompat.IMPORTANCE_DEFAULT
        ).setDescription(context.getString(R.string.rss_news_notification_channel_description))
            .setName(context.getString(R.string.rss_news_notification_channel_name))
            .setShowBadge(true)
            .setVibrationEnabled(true)
            .build()
}