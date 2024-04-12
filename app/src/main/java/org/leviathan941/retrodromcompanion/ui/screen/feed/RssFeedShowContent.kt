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

package org.leviathan941.retrodromcompanion.ui.screen.feed

import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.leviathan941.retrodromcompanion.rssreader.RssChannel
import org.leviathan941.retrodromcompanion.rssreader.asDateTime
import org.leviathan941.retrodromcompanion.ui.navigation.MainNavScreen
import org.leviathan941.retrodromcompanion.ui.screen.webview.WebViewOpener

@Composable
fun RssFeedShowContent(
    channel: RssChannel,
    webViewOpener: WebViewOpener,
) {
    LazyColumn {
        channel.items.forEach { rssFeedItem ->
            item {
                RssFeedItem(
                    modifier = Modifier.clickable {
                        rssFeedItem.run {
                            webViewOpener.open(
                                MainNavScreen.WebView(
                                    title = title,
                                    subtitle = channel.title,
                                    url = link,
                                )
                            )
                        }
                    },
                    title = rssFeedItem.title,
                    categories = rssFeedItem.categories,
                    pubDate = rssFeedItem.pubDate.asDateTime(),
                    imageUrl = rssFeedItem.description?.imageUrl,
                    description = rssFeedItem.description?.paragraphs?.firstOrNull(),
                )
                HorizontalDivider(
                    thickness = 1.dp,
                )
            }
        }
    }
}
