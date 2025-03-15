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

package org.leviathan941.retrodromcompanion.ui.screen.settings.subscreen

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import org.leviathan941.retrodromcompanion.R
import org.leviathan941.retrodromcompanion.firebase.push.Messaging
import org.leviathan941.retrodromcompanion.ui.model.SettingsViewModel
import org.leviathan941.retrodromcompanion.ui.model.ViewModelKeys
import org.leviathan941.retrodromcompanion.ui.openNotificationSettings
import org.leviathan941.retrodromcompanion.ui.permission.NotificationPermissionView
import org.leviathan941.retrodromcompanion.ui.screen.settings.SettingsSwitchItem

@Composable
fun NotificationSettingsSubScreen() {
    val screenViewModel = hiltViewModel<SettingsViewModel>(
        key = ViewModelKeys.SETTINGS_VIEW_MODEL,
    )
    val subscribedPushTopics by screenViewModel.subscribedPushTopics.collectAsState()
    val permissionGranted = remember { mutableStateOf(false) }
    val showRationale = remember { mutableStateOf(false) }
    val context = LocalContext.current

    NotificationPermissionView(
        grantedState = permissionGranted,
        allowRationale = showRationale.value,
        onRationaleDismiss = {
            showRationale.value = false
        },
        onRationaleConfirm = {
            openNotificationSettings(context)
        }
    )

    Column {
        SettingsSwitchItem(
            title = stringResource(id = R.string.settings_notifications_item_push_title),
            checkedIcon = painterResource(
                id = R.drawable.google_material_notifications_bell_on
            ),
            uncheckedIcon = painterResource(
                id = R.drawable.google_material_notifications_bell_off
            ),
            checked = subscribedPushTopics.contains(Messaging.Topic.NEW_RETRODROM_POSTS) &&
                    permissionGranted.value,
            onCheckedChange = { isChecked ->
                if (isChecked) {
                    screenViewModel.subscribeToTopic(
                        topic = Messaging.Topic.NEW_RETRODROM_POSTS,
                    )
                } else {
                    screenViewModel.unsubscribeFromTopic(
                        topic = Messaging.Topic.NEW_RETRODROM_POSTS,
                    )
                }
                if (!permissionGranted.value) {
                    showRationale.value = true
                }
            },
        )
        HorizontalDivider()
    }
}