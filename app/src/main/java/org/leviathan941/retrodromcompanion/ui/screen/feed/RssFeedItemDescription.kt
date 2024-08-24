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
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import org.leviathan941.retrodromcompanion.R
import org.leviathan941.retrodromcompanion.rssreader.asDateTime
import org.leviathan941.retrodromcompanion.ui.navigation.RssFeedDestination
import org.leviathan941.retrodromcompanion.ui.toRssFeedPublicationTime

@Composable
fun RssFeedItemDescription(
    itemDescription: RssFeedDestination.ItemDescription,
    openUrl: (String) -> Unit,
) {
    Column(
        modifier = Modifier
            .padding(horizontal = 10.dp)
            .verticalScroll(
                state = rememberScrollState(),
            ),
        horizontalAlignment = Alignment.Start,
    ) {
        val nonImportantColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)

        Text(
            modifier = Modifier
                .padding(top = 10.dp),
            text = itemDescription.pubDate
                .asDateTime()
                .toRssFeedPublicationTime(LocalContext.current.resources),
            color = nonImportantColor,
            style = MaterialTheme.typography.labelMedium,
        )

        itemDescription.creator?.let { creator ->
            Text(
                modifier = Modifier
                    .padding(bottom = 10.dp),
                text = stringResource(id = R.string.rss_feed_item_description_from_author, creator),
                color = nonImportantColor,
                style = MaterialTheme.typography.labelMedium,
            )
        }

        Text(
            modifier = Modifier
                .padding(bottom = 20.dp),
            text = itemDescription.title,
            style = MaterialTheme.typography.titleLarge,
        )

        itemDescription.imageUrl?.let { imageUrl ->
            AsyncImage(
                modifier = Modifier
                    .padding(bottom = 20.dp),
                model = ImageRequest.Builder(LocalContext.current)
                    .data(imageUrl)
                    .build(),
                contentDescription = null,
                error = painterResource(
                    id = R.drawable.google_material_broken_image
                ),
            )
        }

        itemDescription.paragraphs.forEachIndexed { index, paragraph ->
            if (index == itemDescription.paragraphs.lastIndex) {
                Text(
                    modifier = Modifier
                        .padding(bottom = 20.dp)
                        .clickable { openUrl(itemDescription.link) },
                    text = paragraph,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.primary,
                )
            } else {
                Text(
                    modifier = Modifier
                        .padding(bottom = 20.dp),
                    text = paragraph,
                    style = MaterialTheme.typography.bodyLarge,
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RssFeedItemDescriptionPreview() {
    RssFeedItemDescription(
        itemDescription = RssFeedDestination.ItemDescription(
            title = "What is Lorem Ipsum?",
            link = "https://example.com",
            pubDate = "Mon, 30 Jun 2008 11:05:30 GMT",
            categories = listOf("Category"),
            imageUrl = null,
            paragraphs = listOf(
                "Lorem Ipsum is simply dummy text of the printing and typesetting industry. " +
                        "Lorem Ipsum has been the industry's standard dummy text ever since " +
                        "the 1500s when an unknown printer took a galley of type and scrambled it" +
                        " to make a type specimen book. It has survived not only five centuries, " +
                        "but also the leap into electronic typesetting, remaining essentially " +
                        "unchanged. It was popularised in the 1960s with the release of Letraset " +
                        "sheets containing Lorem Ipsum passages, and more recently with desktop " +
                        "publishing software like Aldus PageMaker including versions of " +
                        "Lorem Ipsum.",
                "Contrary to popular belief, Lorem Ipsum is not simply random text. " +
                        "It has roots in a piece of classical Latin literature from 45 BC, " +
                        "making it over 2000 years old. Richard McClintock, a Latin professor " +
                        "at Hampden-Sydney College in Virginia, looked up one of the more " +
                        "obscure Latin words, consectetur, from a Lorem Ipsum passage, " +
                        "and going through the cites of the word in classical literature, " +
                        "discovered the undoubtable source.",
                "Continue reading...",
            ),
            creator = "Alex Mercer",
        ),
        openUrl = {},
    )
}
