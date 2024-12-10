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

package org.leviathan941.retrodromcompanion.ui.permission

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import org.leviathan941.retrodromcompanion.R
import org.leviathan941.retrodromcompanion.ui.openNotificationSettings

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun PermissionView(
    permission: String,
    grantedState: MutableState<Boolean>,
    rationale: PermissionRationale,
    allowRationale: Boolean = true,
) {
    val permissionState = rememberPermissionState(permission)
    grantedState.value = permissionState.status.isGranted

    if (permissionState.status.isGranted) {
        return
    } else {
        if (permissionState.status.shouldShowRationale) {
            if (allowRationale) {
                PermissionRationaleDialog(
                    data = rationale,
                )
            }
        } else {
            SideEffect {
                permissionState.launchPermissionRequest()
            }
        }
    }
}

@Composable
private fun PermissionRationaleDialog(
    data: PermissionRationale,
) {
    val context = LocalContext.current
    AlertDialog(
        onDismissRequest = {
            data.onDismiss()
        },
        title = {
            Text(
                text = data.title,
                textAlign = TextAlign.Center,
            )
        },
        text = {
            Text(
                text = data.description,
            )
        },
        icon = {
            Icon(
                painter = data.icon,
                contentDescription = null,
            )
        },
        confirmButton = {
            Button(
                onClick = {
                    openNotificationSettings(context)
                    data.onDismiss()
                }
            ) {
                Text(
                    text = stringResource(id = R.string.permission_dialog_confirm_button),
                )
            }
        },
        dismissButton = {
            Button(
                onClick = {
                    data.onDismiss()
                }
            ) {
                Text(
                    text = stringResource(id = R.string.permission_dialog_deny_button),
                )
            }
        }
    )
}

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
        ),
    )
}
