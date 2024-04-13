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

package org.leviathan941.retrodromcompanion.ui.topbar

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import org.leviathan941.retrodromcompanion.R

fun interface TopBarNavButtonListener {
    fun onClicked(button: TopBarNavButton)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBarView(
    prefs: TopBarPrefs,
    navButtonListener: TopBarNavButtonListener,
    onActionClick: (TopBarAction) -> Unit = {},
) {
    TopAppBar(
        modifier = Modifier,
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
        ),
        title = {
            TitleView(
                title = prefs.title,
                subtitle = prefs.subtitle,
            )
        },
        navigationIcon = {
            NavigationIconView(
                navButton = prefs.navButton,
                navButtonListener = navButtonListener,
            )
        },
        actions = {
            ActionsView(
                actions = prefs.actions,
                onActionClick,
            )
        }
    )
}

@Composable
private fun NavigationIconView(
    navButton: TopBarNavButton,
    navButtonListener: TopBarNavButtonListener,
) {
    when (navButton) {
        TopBarNavButton.NONE -> {
            // Show nothing
        }

        TopBarNavButton.BACK -> {
            ButtonView(
                onClick = { navButtonListener.onClicked(TopBarNavButton.BACK) },
                painter = rememberVectorPainter(image = Icons.AutoMirrored.Filled.ArrowBack),
                contentDescRes = R.string.top_bar_navigation_icon_desc_back,
            )
        }

        TopBarNavButton.CLOSE -> {
            ButtonView(
                onClick = { navButtonListener.onClicked(TopBarNavButton.CLOSE) },
                painter = rememberVectorPainter(image = Icons.Filled.Close),
                contentDescRes = R.string.top_bar_navigation_icon_desc_close,
            )
        }

        TopBarNavButton.DRAWER -> {
            ButtonView(
                onClick = { navButtonListener.onClicked(TopBarNavButton.DRAWER) },
                painter = rememberVectorPainter(image = Icons.Filled.Menu),
                contentDescRes = R.string.top_bar_navigation_icon_desc_drawer,
            )
        }
    }
}

@Composable
private fun TitleView(
    title: String,
    subtitle: String,
) {
    Column {
        Text(
            text = title,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = if (subtitle.isEmpty()) {
                MaterialTheme.typography.titleLarge
            } else {
                MaterialTheme.typography.titleMedium
            },
        )
        if (subtitle.isNotEmpty()) {
            Text(
                text = subtitle,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.6f),
            )
        }
    }
}

@Composable
private fun ButtonView(
    onClick: () -> Unit,
    painter: Painter,
    @StringRes contentDescRes: Int,
) {
    IconButton(
        onClick = onClick,
    ) {
        Icon(
            painter = painter,
            contentDescription = stringResource(
                id = contentDescRes
            ),
            tint = MaterialTheme.colorScheme.onPrimaryContainer,
        )
    }
}

@Composable
private fun ActionsView(
    actions: List<TopBarAction> = emptyList(),
    onClick: (TopBarAction) -> Unit = {},
) {
    actions.forEach { action ->
        val onActionClick = { onClick(action) }
        when (action) {
            TopBarAction.BROWSE -> {
                ButtonView(
                    onClick = onActionClick,
                    painter = painterResource(id = R.drawable.google_material_open_in_browser),
                    contentDescRes = R.string.top_bar_action_icon_desc_open_in_browser,
                )
            }
        }
    }
}

@Preview(
    showBackground = true,
)
@Composable
fun TopBarViewOneLineTitlePreview() = TopBarView(
    prefs = TopBarPrefs(
        title = "Title",
        subtitle = "",
        navButton = TopBarNavButton.BACK,
    ),
    navButtonListener = {},
)

@Preview(
    showBackground = true,
)
@Composable
fun TopBarViewTwoLineTitlePreview() = TopBarView(
    prefs = TopBarPrefs(
        title = "Title",
        subtitle = "Subtitle",
        navButton = TopBarNavButton.CLOSE,
    ),
    navButtonListener = {},
)
