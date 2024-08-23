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

package org.leviathan941.retrodromcompanion.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import org.leviathan941.retrodromcompanion.R
import org.leviathan941.retrodromcompanion.ui.navigation.MainNavActions
import org.leviathan941.retrodromcompanion.ui.navigation.RssFeedDestination
import org.leviathan941.retrodromcompanion.ui.topbar.TopBarAction
import org.leviathan941.retrodromcompanion.ui.topbar.TopBarNavButton
import org.leviathan941.retrodromcompanion.ui.topbar.TopBarPrefs
import org.leviathan941.retrodromcompanion.ui.topbar.TopBarView
import org.leviathan941.retrodromcompanion.utils.openUrlInCustomTab

@Composable
fun RssItemDescriptionScreen(
    itemDescription: RssFeedDestination.ItemDescription,
    navigationActions: MainNavActions,
) {
    val context = LocalContext.current
    val toolbarColor = MaterialTheme.colorScheme.primaryContainer.toArgb()
    val openUrl = { url: String ->
        openUrlInCustomTab(
            context = context,
            url = url,
            toolbarColor = toolbarColor,
        )
    }
    Scaffold(
        topBar = {
            TopBarView(
                prefs = TopBarPrefs(
                    title = stringResource(id = R.string.rss_feed_item_description_screen_title),
                    navButton = TopBarNavButton.BACK,
                    actions = listOf(
                        TopBarAction.Browse(itemDescription.link)
                    ),
                ),
                onNavButtonCLick = { navButton ->
                    when (navButton) {
                        TopBarNavButton.BACK -> {
                            navigationActions.navigateBack()
                        }

                        else -> Unit
                    }
                },
                onActionClick = { action ->
                    when (action) {
                        is TopBarAction.Browse -> {
                            openUrl(action.url)
                        }
                    }
                },
            )
        }
    ) { paddings ->
        Box(
            modifier = Modifier.padding(paddings),
        ) {
            Column(
                modifier = Modifier.padding(all = 10.dp),
            ) {
                Text(
                    modifier = Modifier
                        .padding(
                            bottom = 10.dp
                        ),
                    text = itemDescription.title,
                    style = MaterialTheme.typography.titleLarge,
                )

                val actionParagraph = itemDescription.paragraphs.last()
                itemDescription.paragraphs.dropLast(1).forEach { paragraph ->
                    Text(
                        modifier = Modifier.padding(vertical = 5.dp),
                        text = paragraph,
                        style = MaterialTheme.typography.bodyLarge,
                    )
                }
                Text(
                    modifier = Modifier
                        .padding(top = 15.dp)
                        .clickable { openUrl(itemDescription.link) },
                    text = actionParagraph,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
        }
    }
}
