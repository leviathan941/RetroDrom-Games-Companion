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

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import org.leviathan941.retrodromcompanion.ui.navigation.MainNavActions
import org.leviathan941.retrodromcompanion.ui.navigation.RssFeedDestination
import org.leviathan941.retrodromcompanion.ui.screen.feed.RssFeedItemDescription
import org.leviathan941.retrodromcompanion.ui.topbar.TopBarAction
import org.leviathan941.retrodromcompanion.ui.topbar.TopBarNavButton
import org.leviathan941.retrodromcompanion.ui.topbar.TopBarPrefs
import org.leviathan941.retrodromcompanion.ui.topbar.TopBarView
import org.leviathan941.retrodromcompanion.utils.openUrlInCustomTab

@Composable
fun RssItemDescriptionScreen(
    itemDescription: RssFeedDestination.ItemDescription,
    navigationActions: MainNavActions,
    modifier: Modifier = Modifier,
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
        modifier = modifier,
        topBar = {
            TopBarView(
                prefs = TopBarPrefs(
                    title = itemDescription.title,
                    navButton = TopBarNavButton.BACK,
                    actions = listOf(
                        TopBarAction.Browse(itemDescription.link),
                    ),
                ),
                onNavButtonClick = { navButton ->
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
        },
    ) { paddings ->
        Column(
            modifier = Modifier.padding(paddings),
        ) {
            RssFeedItemDescription(
                itemDescription = itemDescription,
                openUrl = openUrl,
            )
        }
    }
}
