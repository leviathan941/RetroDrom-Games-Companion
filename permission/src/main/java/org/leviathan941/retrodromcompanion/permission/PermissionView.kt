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

package org.leviathan941.retrodromcompanion.permission

import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import org.leviathan941.retrodromcompanion.permission.internal.PermissionRationaleDialog

@OptIn(ExperimentalPermissionsApi::class)
@Composable
public fun PermissionView(
    permission: String,
    onPermissionResult: (isGranted: Boolean) -> Unit,
    rationaleData: PermissionRationale.Data,
    allowRationale: Boolean = true,
) {
    val permissionState = rememberPermissionState(permission, onPermissionResult)

    when {
        permissionState.status.isGranted -> {
            SideEffect {
                onPermissionResult(true)
            }
        }

        permissionState.status.shouldShowRationale -> {
            if (allowRationale) {
                PermissionRationaleDialog(
                    data = rationaleData,
                )
            }
        }

        else -> {
            SideEffect {
                permissionState.launchPermissionRequest()
            }
        }
    }
}
