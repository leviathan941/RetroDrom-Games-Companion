/*
 * RetroDrom Games Companion
 * Copyright (C) 2025 Alexey Kuzin <amkuzink@gmail.com>.
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

package org.leviathan941.retrodromcompanion.permission

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.tooling.preview.Preview

@Preview(showBackground = true)
@Composable
private fun PermissionRationaleDialogPreview() {
    PermissionRationaleDialog(
        data = PermissionRationale(
            title = "Big enough title to be shown",
            description = "If you are going to use a passage of Lorem Ipsum, " +
                    "you need to be sure there isn't anything embarrassing hidden in the " +
                    "middle of text.",
            icon = rememberVectorPainter(Icons.Default.Notifications),
            onDismiss = {},
            onConfirm = {},
        ),
    )
}