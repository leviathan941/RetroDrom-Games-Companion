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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import org.leviathan941.retrodromcompanion.R
import org.leviathan941.retrodromcompanion.ui.navigation.MainNavScreen

@Composable
fun RssFeedDrawerNavView(
    rssScreens: List<MainNavScreen.RssFeed>,
    modifier: Modifier = Modifier,
    isSelected: (MainNavScreen.RssFeed) -> Boolean = { false },
    onClick: (MainNavScreen.RssFeed) -> Unit = {},
) {
    DrawerSubMenuView(
        title = stringResource(id = R.string.drawer_rss_feeds_submenu_title),
        modifier = modifier,
        icon = {
            Icon(
                painter = painterResource(id = R.drawable.google_material_news),
                contentDescription = null,
            )
        },
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
