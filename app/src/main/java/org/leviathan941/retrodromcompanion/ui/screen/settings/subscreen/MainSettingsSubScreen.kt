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

package org.leviathan941.retrodromcompanion.ui.screen.settings.subscreen

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import org.leviathan941.retrodromcompanion.BuildConfig
import org.leviathan941.retrodromcompanion.R
import org.leviathan941.retrodromcompanion.ui.model.SettingsViewModel
import org.leviathan941.retrodromcompanion.ui.model.ViewModelKeys
import org.leviathan941.retrodromcompanion.ui.navigation.MainNavActions
import org.leviathan941.retrodromcompanion.ui.navigation.MainNavScreen
import org.leviathan941.retrodromcompanion.ui.navigation.SettingsDestination
import org.leviathan941.retrodromcompanion.ui.screen.SettingsScreen
import org.leviathan941.retrodromcompanion.ui.screen.settings.SettingsClickableNavItem
import org.leviathan941.retrodromcompanion.ui.screen.settings.SettingsGroup
import org.leviathan941.retrodromcompanion.ui.screen.settings.SettingsTitleItem
import org.leviathan941.retrodromcompanion.ui.theme.ThemeType.Companion.toStringResource

@Composable
fun MainSettingsSubScreen(
    navigationActions: MainNavActions,
    screenViewModel: SettingsViewModel = hiltViewModel<SettingsViewModel>(
        key = ViewModelKeys.SETTINGS_VIEW_MODEL,
    ),
) {
    val appTheme by screenViewModel.appTheme.collectAsState()

    SettingsScreen(
        data = MainNavScreen.Settings(
            title = stringResource(id = R.string.settings_screen_title),
        ),
        navigationActions = navigationActions,
    ) {
        SettingsGroup(
            name = stringResource(id = R.string.settings_group_name_interface),
        ) {
            SettingsClickableNavItem(
                title = stringResource(id = R.string.settings_interface_item_theme_title),
                subtitle = stringResource(id = appTheme.toStringResource()),
                leadingIcon = painterResource(id = R.drawable.google_material_contrast),
            ) {
                navigationActions.navigateToSettingsItem(SettingsDestination.AppTheme)
            }

            HorizontalDivider()

            SettingsClickableNavItem(
                title = stringResource(id = R.string.settings_interface_item_notifications_title),
                leadingIcon = rememberVectorPainter(Icons.Default.Notifications),
            ) {
                navigationActions.navigateToSettingsItem(SettingsDestination.Notifications)
            }
        }

        SettingsGroup(
            name = stringResource(id = R.string.settings_group_name_about),
        ) {
            SettingsClickableNavItem(
                title = stringResource(id = R.string.settings_about_item_feedback_title),
                leadingIcon = painterResource(id = R.drawable.google_material_feedback),
            ) {
                navigationActions.navigateToSettingsItem(SettingsDestination.Feedback)
            }

            HorizontalDivider()

            SettingsClickableNavItem(
                title = stringResource(id = R.string.settings_about_item_licenses_title),
                leadingIcon = painterResource(id = R.drawable.google_material_license),
            ) {
                navigationActions.navigateToSettingsItem(SettingsDestination.Licenses)
            }

            HorizontalDivider()

            SettingsTitleItem(
                title = stringResource(id = R.string.settings_about_item_app_version_title),
                subtitle = BuildConfig.VERSION_NAME,
                leadingIcon = painterResource(id = R.drawable.google_material_info),
            )
        }
    }
}
