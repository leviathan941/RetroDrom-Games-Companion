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

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import org.leviathan941.retrodromcompanion.R

@Composable
fun DrawerSubMenuView(
    title: String,
    icon: @Composable (() -> Unit)? = null,
    isExpandedInitially: Boolean = true,
    subMenuContent: @Composable ColumnScope.() -> Unit,
) {
    var isExpanded by remember { mutableStateOf(isExpandedInitially) }

    Column {
        DrawerMenuItemView(
            title = title,
            isSelected = false,
            onClick = { isExpanded = !isExpanded },
            badge = {
                ExpandIcon(isExpanded)
            },
            icon = icon,
        )

        AnimatedVisibility(visible = isExpanded) {
            Column {
                subMenuContent()
            }
        }
    }
}

@Composable
private fun ExpandIcon(isExpanded: Boolean) {
    if (isExpanded) {
        Icon(
            imageVector = Icons.Default.KeyboardArrowDown,
            contentDescription = stringResource(id = R.string.drawer_submenu_item_expanded_desc)
        )
    } else {
        Icon(
            imageVector = Icons.AutoMirrored.Default.KeyboardArrowRight,
            contentDescription = stringResource(id = R.string.drawer_submenu_item_collapsed_desc)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun DrawerSubMenuPreview() = DrawerSubMenuView(
    title = "Sub Menu",
    isExpandedInitially = true,
    subMenuContent = {
        IntRange(1, 5).forEach {
            DrawerMenuItemView(
                title = "Item $it",
            )
        }
    }
)
