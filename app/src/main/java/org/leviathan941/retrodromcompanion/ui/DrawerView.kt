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

package org.leviathan941.retrodromcompanion.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.leviathan941.retrodromcompanion.R
import org.leviathan941.retrodromcompanion.ui.navigation.MainNavActions
import org.leviathan941.retrodromcompanion.ui.navigation.MainNavScreen
import org.leviathan941.retrodromcompanion.ui.theme.DrawerLogoGradientEndColor
import org.leviathan941.retrodromcompanion.ui.theme.DrawerLogoGradientStartColor

@Composable
fun DrawerView(
    currentRoute: String,
    navActions: MainNavActions,
    closeDrawer: () -> Unit,
    rssScreens: List<MainNavScreen.RssFeed>,
) {
    ModalDrawerSheet(
        modifier = Modifier
            .fillMaxWidth(fraction = 0.8f),
    ) {
        DrawerHeader()

        HorizontalDivider(
            modifier = Modifier.padding(bottom = 4.dp),
            thickness = 1.dp,
        )

        RssNavigations(
            rssScreens,
            isSelected = {
                // FIXME: This way does not work
                currentRoute == rssFeedScreenRoute(it.id)
            },
            onClick = {
                closeDrawer()
                navActions.navigateTo(it)
            },
        )
    }
}

@Preview(
    showBackground = true,
)
@Composable
private fun DrawerHeader() {
    val backgroundGradient = Brush.verticalGradient(
        colors = listOf(
            DrawerLogoGradientStartColor,
            DrawerLogoGradientEndColor,
        ),
    )
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(backgroundGradient),
    ) {
        Image(
            modifier = Modifier
                .padding(all = 20.dp),
            painter = painterResource(id = R.drawable.retrodrom_games_words),
            contentDescription = null,
            contentScale = ContentScale.Fit,
        )
    }
}

@Composable
fun RssNavigations(
    rssScreens: List<MainNavScreen.RssFeed>,
    isSelected: (MainNavScreen.RssFeed) -> Boolean = { false },
    onClick: (MainNavScreen.RssFeed) -> Unit = {},
) {
    rssScreens.forEach { rssScreen ->
        NavigationDrawerItem(
            modifier = Modifier
                .height(50.dp),
            label = {
                Text(
                    text = rssScreen.title,
                    style = MaterialTheme.typography.titleMedium,
                )
            },
            selected = isSelected(rssScreen),
            onClick = { onClick(rssScreen) },
            shape = RectangleShape,
        )
    }
}

@Preview(showBackground = true)
@Composable
fun RssNavigationPreview() {
    Column {
        RssNavigations(
            rssScreens = List(5) { i ->
                MainNavScreen.RssFeed(
                    id = i,
                    title = "Category ${i + 1}",
                    channelUrl = "",
                )
            },
        )
    }
}
