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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import org.leviathan941.retrodromcompanion.R
import org.leviathan941.retrodromcompanion.ui.toRssFeedPublicationTime
import java.time.ZoneId
import java.time.ZonedDateTime

@Composable
fun RssFeedItem(
    modifier: Modifier,
    title: String,
    categories: List<String>,
    pubDate: ZonedDateTime,
    imageUrl: String? = null,
    description: String? = null,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(RssFeedItemPaddings),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        val nonImportantColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)

        Row(
            modifier = Modifier.height(55.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (imageUrl != null) {
                AsyncImage(
                    modifier = Modifier
                        .size(50.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(imageUrl)
                        .build(),
                    placeholder = painterResource(
                        id = R.drawable.google_material_image_placeholder
                    ),
                    error = painterResource(
                        id = R.drawable.google_material_broken_image
                    ),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    filterQuality = FilterQuality.Low,
                )
            }
            Column(
                modifier = Modifier
                    .padding(all = 0.dp)
                    .fillMaxHeight(),
            ) {
                Text(
                    text = title,
                    maxLines = 2,
                    style = MaterialTheme.typography.titleMedium,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 18.sp,
                )
                if (description != null) {
                    Text(
                        text = description,
                        maxLines = 2,
                        style = MaterialTheme.typography.bodySmall,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Image(
                modifier = Modifier.size(12.dp),
                painter = painterResource(id = R.drawable.google_material_schedule),
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
    modifier = Modifier,
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
    imageUrl = "https://example.com/image.jpg",
    description = "Clones of the Famicom console, released under local brands, " +
            "are increasingly appearing in the catalogs of some Japanese retail chains.",
)
