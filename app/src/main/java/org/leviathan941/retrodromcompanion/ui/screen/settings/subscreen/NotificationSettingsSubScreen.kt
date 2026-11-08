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
import androidx.compose.ui.Modifier
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import dev.zacsweers.metrox.viewmodel.metroViewModel
import org.leviathan941.retrodromcompanion.resources.Res
import org.leviathan941.retrodromcompanion.resources.google_material_notifications_bell_off
import org.leviathan941.retrodromcompanion.resources.google_material_notifications_bell_on
import org.leviathan941.retrodromcompanion.resources.settings_notifications_item_push_title
import org.leviathan941.retrodromcompanion.notification.PushMessaging
import org.leviathan941.retrodromcompanion.ui.model.SettingsViewModel
import org.leviathan941.retrodromcompanion.ui.model.ViewModelKeys
import org.leviathan941.retrodromcompanion.ui.permission.NotificationPermissionView
import org.leviathan941.retrodromcompanion.ui.screen.settings.SettingsSwitchItem

@Composable
fun NotificationSettingsSubScreen(modifier: Modifier = Modifier) {
    val screenViewModel = metroViewModel<SettingsViewModel>(
        key = ViewModelKeys.SETTINGS_VIEW_MODEL,
    )
    val subscribedPushTopics by screenViewModel.subscribedPushTopics.collectAsState()
    NotificationSettingsContent(
        pushPostsSubscribed = PushMessaging.Topic.NEW_RETRODROM_POSTS in subscribedPushTopics,
        onPushPostsSubscribedChange = { isSubscribed ->
            if (isSubscribed) {
                screenViewModel.subscribeToTopic(
                    topic = PushMessaging.Topic.NEW_RETRODROM_POSTS,
                )
            } else {
                screenViewModel.unsubscribeFromTopic(
                    topic = PushMessaging.Topic.NEW_RETRODROM_POSTS,
                )
            }
        },
        modifier = modifier,
    )
}

@Composable
private fun NotificationSettingsContent(
    pushPostsSubscribed: Boolean,
    onPushPostsSubscribedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val permissionGranted = remember { mutableStateOf(false) }
    val showRationale = remember { mutableStateOf(false) }

    NotificationPermissionView(
        onPermissionResult = { isGranted ->
            permissionGranted.value = isGranted
        },
        allowRationale = showRationale.value,
        onRationaleDismiss = {
            showRationale.value = false
        },
    )

    Column(
        modifier = modifier,
    ) {
        SettingsSwitchItem(
            title = stringResource(Res.string.settings_notifications_item_push_title),
            checkedIcon = painterResource(
                Res.drawable.google_material_notifications_bell_on,
            ),
            uncheckedIcon = painterResource(
                Res.drawable.google_material_notifications_bell_off,
            ),
            checked = pushPostsSubscribed && permissionGranted.value,
            onCheckedChange = { isChecked ->
                onPushPostsSubscribedChange(isChecked)
                if (!permissionGranted.value) {
                    showRationale.value = true
                }
            },
        )
        HorizontalDivider()
    }
}
