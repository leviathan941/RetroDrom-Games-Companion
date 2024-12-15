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

import android.Manifest
import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import org.leviathan941.retrodromcompanion.R
import org.leviathan941.retrodromcompanion.permission.PermissionRationale
import org.leviathan941.retrodromcompanion.permission.PermissionView
import org.leviathan941.retrodromcompanion.ui.openNotificationSettings

@Composable
fun NotificationPermissionView(
    grantedState: MutableState<Boolean>,
    allowRationale: Boolean,
    onRationaleDismiss: () -> Unit,
) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        val context = LocalContext.current
        PermissionView(
            permission = Manifest.permission.POST_NOTIFICATIONS,
            grantedState = grantedState,
            rationale = PermissionRationale(
                title = stringResource(id = R.string.notification_permission_dialog_title),
                description = stringResource(id = R.string.notification_permission_dialog_text),
                icon = painterResource(id = R.drawable.google_material_notifications_bell_on),
                onDismiss = onRationaleDismiss,
                onConfirm = {
                    openNotificationSettings(context)
                }
            ),
            allowRationale = allowRationale,
        )
    } else {
        grantedState.value = true
    }
}