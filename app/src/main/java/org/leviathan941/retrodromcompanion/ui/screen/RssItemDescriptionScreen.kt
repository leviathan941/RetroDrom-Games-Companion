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
import dev.zacsweers.metrox.viewmodel.metroViewModel
import kotlinx.collections.immutable.persistentListOf
import org.leviathan941.retrodromcompanion.resources.Res
import org.leviathan941.retrodromcompanion.resources.open_external_link_application_not_found
import org.leviathan941.retrodromcompanion.app.message.UserMessage
import org.leviathan941.retrodromcompanion.browser.rememberInAppBrowser
import org.leviathan941.retrodromcompanion.ui.model.UserMessagesViewModel
import org.leviathan941.retrodromcompanion.ui.model.ViewModelKeys
import org.leviathan941.retrodromcompanion.ui.navigation.MainNavActions
import org.leviathan941.retrodromcompanion.ui.navigation.RssFeedDestination
import org.leviathan941.retrodromcompanion.ui.screen.feed.RssFeedItemDescription
import org.leviathan941.retrodromcompanion.ui.topbar.TopBarAction
import org.leviathan941.retrodromcompanion.ui.topbar.TopBarNavButton
import org.leviathan941.retrodromcompanion.ui.topbar.TopBarPrefs
import org.leviathan941.retrodromcompanion.ui.topbar.TopBarView

@Composable
fun RssItemDescriptionScreen(
    itemDescription: RssFeedDestination.ItemDescription,
    navigationActions: MainNavActions,
    modifier: Modifier = Modifier,
) {
    val userMessagesViewModel = metroViewModel<UserMessagesViewModel>(
        key = ViewModelKeys.USER_MESSAGES_VIEW_MODEL,
    )
    val inAppBrowser = rememberInAppBrowser(
        toolbarColor = MaterialTheme.colorScheme.primaryContainer,
    )
    val openUrl = { url: String ->
        if (!inAppBrowser.open(url)) {
            userMessagesViewModel.show(
                UserMessage.Resource(Res.string.open_external_link_application_not_found),
            )
        }
    }
    Scaffold(
        modifier = modifier,
        topBar = {
            TopBarView(
                prefs = TopBarPrefs(
                    title = itemDescription.title,
                    navButton = TopBarNavButton.BACK,
                    actions = persistentListOf(
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
