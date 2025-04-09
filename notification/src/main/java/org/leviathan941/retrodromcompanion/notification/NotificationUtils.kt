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

import android.content.Intent
import android.net.Uri
import org.leviathan941.retrodromcompanion.common.Constants

private const val RETRODROM_DEEPLINK_SCHEME = "retrodrom"

private const val RETRODROM_FEED_HOST = "feed"
private const val RETRODROM_FEED_ITEM_PATH = "item"
public const val RETRODROM_FEED_ITEM_QUERY_CHANNEL_URL: String = "channelUrl"

public const val RETRODROM_FEED_ITEM_BASE_PATH: String =
    "$RETRODROM_DEEPLINK_SCHEME://$RETRODROM_FEED_HOST/$RETRODROM_FEED_ITEM_PATH"

public const val POST_ID_PAYLOAD_DATA_KEY: String = "post_id"

public fun createFeedItemDeeplink(postId: String): Uri? {
    if (postId.isEmpty()) return null

    return Uri.Builder()
        .scheme(RETRODROM_DEEPLINK_SCHEME)
        .authority(RETRODROM_FEED_HOST)
        .appendPath(RETRODROM_FEED_ITEM_PATH)
        .appendPath(postId)
        .appendQueryParameter(RETRODROM_FEED_ITEM_QUERY_CHANNEL_URL, Constants.RETRODROM_BASE_URL)
        .build()
}

public fun extractDeeplink(intent: Intent): Uri? {
    return with(intent) {
        data ?: extras?.let { bundle ->
            bundle.getString(POST_ID_PAYLOAD_DATA_KEY)?.let { postId ->
                createFeedItemDeeplink(postId)
            }
        }
    }?.takeIf { it.isSupportedDeeplink() }
}

private fun Uri.isSupportedDeeplink(): Boolean {
    return scheme == RETRODROM_DEEPLINK_SCHEME
}
