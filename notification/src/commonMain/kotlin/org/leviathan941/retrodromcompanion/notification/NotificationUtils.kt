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

package org.leviathan941.retrodromcompanion.notification

import io.ktor.http.URLBuilder
import io.ktor.http.URLProtocol
import io.ktor.http.appendPathSegments
import io.ktor.http.parseUrl
import org.leviathan941.retrodromcompanion.common.Constants

private const val RETRODROM_DEEPLINK_SCHEME = "retrodrom"

private const val RETRODROM_FEED_HOST = "feed"
private const val RETRODROM_FEED_ITEM_PATH = "item"
public const val RETRODROM_FEED_ITEM_QUERY_CHANNEL_URL: String = "channelUrl"

public const val RETRODROM_FEED_ITEM_BASE_PATH: String =
    "$RETRODROM_DEEPLINK_SCHEME://$RETRODROM_FEED_HOST/$RETRODROM_FEED_ITEM_PATH"

public const val POST_ID_PAYLOAD_DATA_KEY: String = "post_id"

public fun createFeedItemDeeplink(postId: String): String? {
    if (postId.isEmpty()) return null

    return URLBuilder(
        protocol = URLProtocol.createOrDefault(RETRODROM_DEEPLINK_SCHEME),
        host = RETRODROM_FEED_HOST,
    ).apply {
        appendPathSegments(RETRODROM_FEED_ITEM_PATH, postId, encodeSlash = true)
        parameters.append(RETRODROM_FEED_ITEM_QUERY_CHANNEL_URL, Constants.RETRODROM_BASE_URL)
    }.buildString()
}

/**
 * Picks the deeplink a launch carries: [data] wins when present, otherwise one is built from the
 * push payload's [postId]. Returns `null` unless the result uses the app's own scheme.
 */
public fun extractDeeplink(
    data: String?,
    postId: String?,
): String? = (data ?: postId?.let(::createFeedItemDeeplink))
    ?.takeIf { it.isSupportedDeeplink() }

private fun String.isSupportedDeeplink(): Boolean =
    parseUrl(this)?.protocol?.name == RETRODROM_DEEPLINK_SCHEME
