@file:Suppress("MagicNumber")
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

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.Clipboard
import androidx.compose.ui.platform.UriHandler
import org.jetbrains.compose.resources.PluralStringResource
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.stringResource
import org.leviathan941.retrodromcompanion.resources.Res
import org.leviathan941.retrodromcompanion.resources.open_email_application_not_found
import org.leviathan941.retrodromcompanion.resources.open_external_link_application_not_found
import org.leviathan941.retrodromcompanion.resources.rss_feed_item_publication_time_days
import org.leviathan941.retrodromcompanion.resources.rss_feed_item_publication_time_hours
import org.leviathan941.retrodromcompanion.resources.rss_feed_item_publication_time_just_now
import org.leviathan941.retrodromcompanion.resources.rss_feed_item_publication_time_minutes
import org.leviathan941.retrodromcompanion.resources.rss_feed_item_publication_time_months
import org.leviathan941.retrodromcompanion.resources.rss_feed_item_publication_time_years
import org.leviathan941.retrodromcompanion.app.message.UserMessage
import org.leviathan941.retrodromcompanion.rssreader.RssChannelItem
import org.leviathan941.retrodromcompanion.ui.navigation.MainNavScreen
import org.leviathan941.retrodromcompanion.ui.navigation.RssFeedDestination
import kotlin.time.Clock
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.Instant

internal const val FEEDBACK_URL = "https://retrodrom.games/feedback/"
internal const val FEEDBACK_EMAIL = "retrodrom.games@gmail.com"

internal const val NAVIGATION_ANIMATION_DURATION_MS = 300

@Composable
internal fun Instant.toRssFeedPublicationTime(): String {
    val age = Clock.System.now() - this
    return when {
        age.inWholeMinutes < 1L ->
            stringResource(Res.string.rss_feed_item_publication_time_just_now)

        age.inWholeHours < 1L ->
            timeQuantityString(
                Res.plurals.rss_feed_item_publication_time_minutes,
                age,
                DurationUnit.MINUTES,
            )

        age.inWholeDays < 1L ->
            timeQuantityString(
                Res.plurals.rss_feed_item_publication_time_hours,
                age,
                DurationUnit.HOURS,
            )

        age.inWholeDays < 30L ->
            timeQuantityString(
                Res.plurals.rss_feed_item_publication_time_days,
                age,
                DurationUnit.DAYS,
            )

        age.inWholeDays < 365L ->
            (age.inWholeDays / 30L).let { diffMonths ->
                pluralStringResource(
                    Res.plurals.rss_feed_item_publication_time_months,
                    diffMonths.toInt(),
                    diffMonths,
                )
            }

        else ->
            (age.inWholeDays / 365L).let { diffYears ->
                pluralStringResource(
                    Res.plurals.rss_feed_item_publication_time_years,
                    diffYears.toInt(),
                    diffYears,
                )
            }
    }
}

@Composable
private fun timeQuantityString(
    resource: PluralStringResource,
    duration: Duration,
    durationUnit: DurationUnit,
): String = duration.toInt(durationUnit).let { quantity ->
    pluralStringResource(resource, quantity, quantity)
}

internal fun RssChannelItem.toDestination(): RssFeedDestination.ItemDescription? =
    description?.let { desc ->
        RssFeedDestination.ItemDescription(
            title = title,
            link = link,
            pubDate = pubDate.value,
            html = desc.html,
            creator = creator,
        )
    }

internal fun MainNavScreen.RssFeed.toDestination(): RssFeedDestination.Feed =
    RssFeedDestination.Feed(id, title, channelUrl)

internal fun RssFeedDestination.Feed.toScreen(): MainNavScreen.RssFeed =
    MainNavScreen.RssFeed(id, title, channelUrl)

internal fun UriHandler.openUrl(
    url: String,
    showMessage: (UserMessage) -> Unit,
) = openUriOrReport(
    uri = url,
    showMessage = showMessage,
    notFoundMessage = UserMessage.Resource(Res.string.open_external_link_application_not_found),
)

internal fun UriHandler.openEmail(
    email: String,
    showMessage: (UserMessage) -> Unit,
) = openUriOrReport(
    uri = "mailto:$email",
    showMessage = showMessage,
    notFoundMessage = UserMessage.Resource(Res.string.open_email_application_not_found),
)

// UriHandler.openUri throws IllegalArgumentException when no app can open the URI.
private fun UriHandler.openUriOrReport(
    uri: String,
    showMessage: (UserMessage) -> Unit,
    notFoundMessage: UserMessage,
) {
    try {
        openUri(uri)
    } catch (_: IllegalArgumentException) {
        showMessage(notFoundMessage)
    }
}

internal suspend fun copyToClipboard(
    clipboard: Clipboard,
    label: CharSequence,
    text: CharSequence,
    showMessage: (UserMessage) -> Unit,
) {
    clipboard.setClipEntry(plainTextClipEntry(label, text))
    if (!systemConfirmsClipboardCopy()) {
        showMessage(UserMessage.Text(label.toString()))
    }
}

/** A clipboard entry holding plain [text], described by [label] where the platform keeps one. */
internal expect fun plainTextClipEntry(
    label: CharSequence,
    text: CharSequence,
): ClipEntry

/** Whether the platform confirms a copy to the clipboard itself, so the app need not. */
internal expect fun systemConfirmsClipboardCopy(): Boolean
