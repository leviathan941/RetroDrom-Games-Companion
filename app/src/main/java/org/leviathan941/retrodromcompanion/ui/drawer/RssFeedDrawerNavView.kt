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

package org.leviathan941.retrodromcompanion.ui.drawer

import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import kotlinx.collections.immutable.ImmutableList
import org.leviathan941.retrodromcompanion.resources.Res
import org.leviathan941.retrodromcompanion.resources.drawer_rss_feeds_submenu_title
import org.leviathan941.retrodromcompanion.resources.google_material_news
import org.leviathan941.retrodromcompanion.ui.navigation.MainNavScreen

@Composable
fun RssFeedDrawerNavView(
    rssScreens: ImmutableList<MainNavScreen.RssFeed>,
    modifier: Modifier = Modifier,
    isSelected: (MainNavScreen.RssFeed) -> Boolean = { false },
    onClick: (MainNavScreen.RssFeed) -> Unit = {},
) {
    DrawerSubMenuView(
        title = stringResource(Res.string.drawer_rss_feeds_submenu_title),
        modifier = modifier,
        icon = {
            Icon(
                painter = painterResource(Res.drawable.google_material_news),
                contentDescription = null,
            )
        },
        isExpandedInitially = false,
    ) {
        rssScreens.sortedBy { it.id }.forEach { rssScreen ->
            DrawerMenuItemView(
                title = rssScreen.title,
                isSelected = isSelected(rssScreen),
                nestingLevel = 1,
                onClick = { onClick(rssScreen) },
            )
        }
    }
}
