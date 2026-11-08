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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import dev.zacsweers.metrox.viewmodel.metroViewModel
import org.leviathan941.retrodromcompanion.BuildConfig
import org.leviathan941.retrodromcompanion.resources.Res
import org.leviathan941.retrodromcompanion.resources.google_material_contrast
import org.leviathan941.retrodromcompanion.resources.google_material_feedback
import org.leviathan941.retrodromcompanion.resources.google_material_info
import org.leviathan941.retrodromcompanion.resources.google_material_license
import org.leviathan941.retrodromcompanion.resources.settings_about_item_app_version_title
import org.leviathan941.retrodromcompanion.resources.settings_about_item_feedback_title
import org.leviathan941.retrodromcompanion.resources.settings_about_item_licenses_title
import org.leviathan941.retrodromcompanion.resources.settings_group_name_about
import org.leviathan941.retrodromcompanion.resources.settings_group_name_interface
import org.leviathan941.retrodromcompanion.resources.settings_interface_item_notifications_title
import org.leviathan941.retrodromcompanion.resources.settings_interface_item_theme_title
import org.leviathan941.retrodromcompanion.resources.settings_screen_title
import org.leviathan941.retrodromcompanion.ui.model.SettingsViewModel
import org.leviathan941.retrodromcompanion.ui.model.ViewModelKeys
import org.leviathan941.retrodromcompanion.ui.navigation.MainNavActions
import org.leviathan941.retrodromcompanion.ui.navigation.MainNavScreen
import org.leviathan941.retrodromcompanion.ui.navigation.SettingsDestination
import org.leviathan941.retrodromcompanion.ui.screen.SettingsScreen
import org.leviathan941.retrodromcompanion.ui.screen.settings.SettingsClickableItem
import org.leviathan941.retrodromcompanion.ui.screen.settings.SettingsGroup
import org.leviathan941.retrodromcompanion.ui.screen.settings.SettingsTitleItem
import org.leviathan941.retrodromcompanion.ui.screen.settings.rememberSettingsNavigationIcon
import org.leviathan941.retrodromcompanion.ui.theme.ThemeType
import org.leviathan941.retrodromcompanion.ui.theme.ThemeType.Companion.toStringResource

@Composable
fun MainSettingsSubScreen(
    navigationActions: MainNavActions,
    modifier: Modifier = Modifier,
) {
    val screenViewModel = metroViewModel<SettingsViewModel>(
        key = ViewModelKeys.SETTINGS_VIEW_MODEL,
    )
    val appTheme by screenViewModel.appTheme.collectAsState()
    MainSettingsContent(
        appTheme = appTheme,
        navigationActions = navigationActions,
        modifier = modifier,
    )
}

@Composable
private fun MainSettingsContent(
    appTheme: ThemeType,
    navigationActions: MainNavActions,
    modifier: Modifier = Modifier,
) {
    SettingsScreen(
        data = MainNavScreen.Settings(
            title = stringResource(Res.string.settings_screen_title),
        ),
        navigationActions = navigationActions,
        modifier = modifier,
    ) {
        SettingsGroup(
            title = stringResource(Res.string.settings_group_name_interface),
        ) {
            SettingsClickableItem(
                title = stringResource(Res.string.settings_interface_item_theme_title),
                subtitle = stringResource(appTheme.toStringResource()),
                leadingIcon = painterResource(Res.drawable.google_material_contrast),
                trailingIcon = rememberSettingsNavigationIcon(),
            ) {
                navigationActions.navigateToSettingsItem(SettingsDestination.AppTheme)
            }

            HorizontalDivider()

            SettingsClickableItem(
                title = stringResource(Res.string.settings_interface_item_notifications_title),
                leadingIcon = rememberVectorPainter(Icons.Default.Notifications),
                trailingIcon = rememberSettingsNavigationIcon(),
            ) {
                navigationActions.navigateToSettingsItem(SettingsDestination.Notifications)
            }
        }

        SettingsGroup(
            title = stringResource(Res.string.settings_group_name_about),
        ) {
            SettingsClickableItem(
                title = stringResource(Res.string.settings_about_item_feedback_title),
                leadingIcon = painterResource(Res.drawable.google_material_feedback),
                trailingIcon = rememberSettingsNavigationIcon(),
            ) {
                navigationActions.navigateToSettingsItem(SettingsDestination.Feedback)
            }

            HorizontalDivider()

            SettingsClickableItem(
                title = stringResource(Res.string.settings_about_item_licenses_title),
                leadingIcon = painterResource(Res.drawable.google_material_license),
                trailingIcon = rememberSettingsNavigationIcon(),
            ) {
                navigationActions.navigateToSettingsItem(SettingsDestination.Licenses)
            }

            HorizontalDivider()

            SettingsTitleItem(
                title = stringResource(Res.string.settings_about_item_app_version_title),
                subtitle = BuildConfig.VERSION_NAME,
                leadingIcon = painterResource(Res.drawable.google_material_info),
            )
        }
    }
}
