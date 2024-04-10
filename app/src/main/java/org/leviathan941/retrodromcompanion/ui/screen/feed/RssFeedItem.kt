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

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.leviathan941.retrodromcompanion.R
import org.leviathan941.retrodromcompanion.ui.toRssFeedPublicationTime
import java.time.ZoneId
import java.time.ZonedDateTime

@Composable
fun RssFeedItem(
    title: String,
    categories: List<String>,
    pubDate: ZonedDateTime,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 15.dp, top = 8.dp, end = 15.dp, bottom = 10.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        val nonImportantColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
        Text(
            text = title,
            maxLines = 2,
            style = MaterialTheme.typography.titleMedium,
            overflow = TextOverflow.Ellipsis,
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Image(
                modifier = Modifier.size(12.dp),
                painter = painterResource(id = R.drawable.rss_post_pub_date),
                contentDescription = stringResource(
                    id = R.string.rss_feed_item_publication_icon_desc
                ),
                colorFilter = ColorFilter.tint(
                    color = nonImportantColor,
                ),
            )
            Text(
                text = pubDate.toRssFeedPublicationTime(LocalContext.current.resources),
                maxLines = 1,
                style = MaterialTheme.typography.labelMedium,
                color = nonImportantColor,
            )

            if (categories.isNotEmpty()) {
                Text(
                    text = "\u2014",
                    style = MaterialTheme.typography.labelMedium,
                    color = nonImportantColor
                )
                Text(
                    text = categories.joinToString(", "),
                    style = MaterialTheme.typography.labelMedium,
                    color = nonImportantColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

@Preview(
    showBackground = true,
)
@Composable
fun RssFeedItemPreview() = RssFeedItem(
    title = "Very interesting news! Be hurry to read it! Do not miss!",
    categories = listOf(
        "Category 1",
        "Category 2",
        "Category 3",
        "Category 4",
        "Category 5",
        "Category 6"
    ),
    pubDate = ZonedDateTime.of(2024, 3, 1, 12, 0, 0, 0, ZoneId.systemDefault()),
)
