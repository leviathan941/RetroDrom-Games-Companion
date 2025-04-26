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

package org.leviathan941.retrodromcompanion.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import org.leviathan941.retrodromcompanion.BuildConfig
import org.leviathan941.retrodromcompanion.R
import org.leviathan941.retrodromcompanion.ui.model.SettingsViewModel
import org.leviathan941.retrodromcompanion.ui.model.ViewModelKeys
import org.leviathan941.retrodromcompanion.ui.screen.SettingsScreen
import org.leviathan941.retrodromcompanion.ui.screen.settings.SettingsClickableNavItem
import org.leviathan941.retrodromcompanion.ui.screen.settings.SettingsGroup
import org.leviathan941.retrodromcompanion.ui.screen.settings.SettingsRadioGroup
import org.leviathan941.retrodromcompanion.ui.screen.settings.SettingsRadioGroupItem
import org.leviathan941.retrodromcompanion.ui.screen.settings.SettingsTitleItem
import org.leviathan941.retrodromcompanion.ui.screen.settings.subscreen.FeedbackSettingsSubScreen
import org.leviathan941.retrodromcompanion.ui.screen.settings.subscreen.NotificationSettingsSubScreen
import org.leviathan941.retrodromcompanion.ui.theme.ThemeType
import org.leviathan941.retrodromcompanion.ui.theme.ThemeType.Companion.toStringResource

fun NavGraphBuilder.settingsNavHost(
    navigationActions: MainNavActions,
) {
    composable<SettingsDestination.Main> {
        val screenViewModel = hiltViewModel<SettingsViewModel>(
            key = ViewModelKeys.SETTINGS_VIEW_MODEL,
        )
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
                name = stringResource(id = R.string.settings_group_name_about)
            ) {
                SettingsClickableNavItem(
                    title = stringResource(id = R.string.settings_about_item_feedback_title),
                    leadingIcon = painterResource(id = R.drawable.google_material_feedback),
                ) {
                    navigationActions.navigateToSettingsItem(SettingsDestination.Feedback)
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

    composable<SettingsDestination.Notifications> {
        SettingsScreen(
            data = MainNavScreen.Settings(
                title = stringResource(id = R.string.settings_interface_item_notifications_title),
            ),
            navigationActions = navigationActions,
        ) {
            NotificationSettingsSubScreen()
        }
    }

    composable<SettingsDestination.AppTheme> {
        val screenViewModel = hiltViewModel<SettingsViewModel>(
            key = ViewModelKeys.SETTINGS_VIEW_MODEL,
        )
        val appTheme by screenViewModel.appTheme.collectAsState()
        val themeItems = ThemeType.supportedEntries().map {
            SettingsRadioGroupItem(
                value = it,
                title = stringResource(id = it.toStringResource()),
            )
        }
        val selectedItem = themeItems.find { it.value == appTheme } ?: themeItems.first()
        SettingsScreen(
            data = MainNavScreen.Settings(
                title = stringResource(id = R.string.settings_interface_item_theme_title),
            ),
            navigationActions = navigationActions,
        ) {
            SettingsRadioGroup(
                selectedItem = selectedItem,
                items = themeItems
            ) {
                screenViewModel.setAppTheme(it.value)
            }
        }
    }

    composable<SettingsDestination.Feedback> {
        SettingsScreen(
            data = MainNavScreen.Settings(
                title = stringResource(id = R.string.settings_about_item_feedback_title),
            ),
            navigationActions = navigationActions,
        ) {
            FeedbackSettingsSubScreen()
        }
    }
}
