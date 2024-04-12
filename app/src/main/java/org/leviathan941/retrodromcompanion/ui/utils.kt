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

package org.leviathan941.retrodromcompanion.ui

import android.content.res.Resources
import androidx.annotation.PluralsRes
import androidx.navigation.NavHostController
import org.leviathan941.retrodromcompanion.R
import org.leviathan941.retrodromcompanion.ui.model.MainViewState
import org.leviathan941.retrodromcompanion.ui.navigation.MainDestination
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.toDuration

internal const val MAIN_VIEW_TAG = "MainView"
internal const val BASE_URL = "https://retrodrom.games/"
internal const val BASE_TITLE = "RetroDrom Games"
internal const val RSS_FEED_NAV_CHANNEL_ID = "rss_feed_channel_id"

fun ZonedDateTime.toRssFeedPublicationTime(
    resources: Resources,
): String {
    val now = ZonedDateTime.now(zone)
    val diffMinutes = ChronoUnit.MINUTES.between(this, now)
        .toDuration(DurationUnit.MINUTES)
    return when {
        diffMinutes.inWholeMinutes < 1L ->
            resources.getString(R.string.rss_feed_item_publication_time_just_now)

        diffMinutes.inWholeHours < 1L ->
            resources.getTimeQuantityString(
                R.plurals.rss_feed_item_publication_time_minutes,
                diffMinutes,
                DurationUnit.MINUTES
            )

        diffMinutes.inWholeDays < 1L ->
            resources.getTimeQuantityString(
                R.plurals.rss_feed_item_publication_time_hours,
                diffMinutes,
                DurationUnit.HOURS
            )

        diffMinutes.inWholeDays < 30L ->
            resources.getTimeQuantityString(
                R.plurals.rss_feed_item_publication_time_days,
                diffMinutes,
                DurationUnit.DAYS
            )

        diffMinutes.inWholeDays < 365L ->
            (diffMinutes.inWholeDays / 30L).let { diffMonths ->
                resources.getQuantityString(
                    R.plurals.rss_feed_item_publication_time_months,
                    diffMonths.toInt(),
                    diffMonths
                )
            }

        else ->
            (diffMinutes.inWholeDays / 365L).let { diffYears ->
                resources.getQuantityString(
                    R.plurals.rss_feed_item_publication_time_years,
                    diffYears.toInt(),
                    diffYears
                )
            }
    }
}

internal fun NavHostController.currentDestinationUrl(
    uiState: MainViewState,
): String? = currentDestination?.route?.let { route ->
    when {
        route.startsWith(MainDestination.RSS_FEED.route) -> {
            route.substringAfterLast('/').toIntOrNull()?.let {
                uiState.rssFeedData.getOrNull(it)?.channelUrl
            }
        }

        route == MainDestination.WEB_VIEW.route -> uiState.webViewData?.url

        else -> null
    }
}

private fun Resources.getTimeQuantityString(
    @PluralsRes id: Int,
    duration: Duration,
    durationUnit: DurationUnit,
): String = duration.toInt(durationUnit).let { quantity ->
    getQuantityString(id, quantity, quantity)
}
