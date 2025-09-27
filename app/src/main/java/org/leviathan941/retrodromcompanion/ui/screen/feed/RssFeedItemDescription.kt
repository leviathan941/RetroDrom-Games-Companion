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

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.leviathan941.compose.htmltext.HtmlText
import org.leviathan941.compose.htmltext.imagecontent.ImageContentCreator
import org.leviathan941.retrodromcompanion.R
import org.leviathan941.retrodromcompanion.rssreader.asDateTime
import org.leviathan941.retrodromcompanion.ui.navigation.RssFeedDestination
import org.leviathan941.retrodromcompanion.ui.toRssFeedPublicationTime

@Composable
fun RssFeedItemDescription(
    itemDescription: RssFeedDestination.ItemDescription,
    openUrl: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val horizontalPaddingDp = 10.dp
    Column(
        modifier = modifier
            .padding(horizontal = horizontalPaddingDp)
            .fillMaxHeight()
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

        val defaultImageWidth = with(LocalConfiguration.current) {
            with(LocalDensity.current) {
                (screenWidthDp.dp - horizontalPaddingDp * 2).toSp()
            }
        }

        @SuppressWarnings("MagicNumber")
        val defaultImageHeight = defaultImageWidth * 0.75f
        HtmlText(
            html = itemDescription.html,
            textStyle = MaterialTheme.typography.bodyLarge,
            linkColor = MaterialTheme.colorScheme.primary,
            inlineContentCreators = listOf(
                ImageContentCreator(
                    localDensity = LocalDensity.current,
                    modifier = Modifier.wrapContentSize(align = Alignment.TopStart),
                    placeholder = painterResource(R.drawable.google_material_image_placeholder),
                    error = painterResource(R.drawable.google_material_broken_image),
                    defaultWidth = defaultImageWidth,
                    defaultHeight = defaultImageHeight,
                ),
            ),
            onLinkClick = { url ->
                openUrl(url)
            },
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun RssFeedItemDescriptionPreview() {
    RssFeedItemDescription(
        itemDescription = RssFeedDestination.ItemDescription(
            title = "What is Lorem Ipsum?",
            link = "https://example.com",
            pubDate = "Mon, 30 Jun 2008 11:05:30 GMT",
            html = """
                <h2>What is Lorem Ipsum?</h2>
                <p><strong>Lorem Ipsum</strong> is simply dummy text of the printing and 
                typesetting industry.
                Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, 
                when an unknown printer took a galley of type and scrambled it to make a type 
                specimen book. It has survived not only five centuries, but also the leap into 
                electronic typesetting, remaining essentially unchanged. It was popularised in 
                the 1960s with the release of Letraset sheets containing Lorem Ipsum passages, 
                and more recently with desktop publishing software like Aldus PageMaker including 
                versions of Lorem Ipsum.</p>
                <h2>Why do we use it?</h2>
                <p>It is a long established fact that a reader will be distracted by the 
                readable content of a page when looking at its layout. The point of using 
                Lorem Ipsum is that it has a more-or-less normal distribution of letters, 
                as opposed to using 'Content here, content here', making it look like readable 
                English. Many desktop publishing packages and web page editors now use Lorem Ipsum 
                as their default model text, and a search for 'lorem ipsum' will uncover many web 
                sites still in their infancy. Various versions have evolved over the years, 
                sometimes by accident, sometimes on purpose (injected humour and the like).</p>
                </div><br /><div>
                <h2>Where does it come from?</h2>
                <p>Contrary to popular belief, Lorem Ipsum is not simply random text. It has 
                roots in a piece of classical Latin literature from 45 BC, making it over 2000 
                years old. Richard McClintock, a Latin professor at Hampden-Sydney College in 
                Virginia, looked up one of the more obscure Latin words, consectetur, from a 
                Lorem Ipsum passage, and going through the cites of the word in classical 
                literature, discovered the undoubtable source. Lorem Ipsum comes from sections 
                1.10.32 and 1.10.33 of "de Finibus Bonorum et Malorum" (The Extremes of Good and 
                Evil) by Cicero, written in 45 BC. This book is a treatise on the theory of 
                ethics, very popular during the Renaissance. The first line of Lorem Ipsum, "Lorem 
                ipsum dolor sit amet..", comes from a line in section 1.10.32.</p>
            """.trimIndent(),
            creator = "Alex Mercer",
        ),
        openUrl = {},
    )
}
