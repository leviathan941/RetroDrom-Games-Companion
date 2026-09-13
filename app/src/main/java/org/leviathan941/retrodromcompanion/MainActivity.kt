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

package org.leviathan941.retrodromcompanion

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import org.leviathan941.retrodromcompanion.app.migration.AppDataMigrationState
import org.leviathan941.retrodromcompanion.app.migration.AppDataMigrator
import org.leviathan941.retrodromcompanion.common.logging.Logger
import org.leviathan941.retrodromcompanion.notification.extractDeeplink
import org.leviathan941.retrodromcompanion.ui.MainView
import org.leviathan941.retrodromcompanion.ui.model.ViewModelKeys
import org.leviathan941.retrodromcompanion.ui.theme.MainTheme
import org.leviathan941.retrodromcompanion.ui.theme.SecondThemeColorScheme
import org.leviathan941.retrodromcompanion.ui.theme.ThemeViewModel

private val logger = Logger.withTag("MainActivity")

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject lateinit var appDataMigrator: AppDataMigrator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen().setKeepOnScreenCondition {
            appDataMigrator.state.value !is AppDataMigrationState.Finished
        }
        setContent {
            val themeViewModel = hiltViewModel<ThemeViewModel>(
                key = ViewModelKeys.THEME_VIEW_MODEL,
            )
            val appTheme by themeViewModel.appTheme.collectAsState()
            val navController = rememberNavController()
            val migrationState by appDataMigrator.state.collectAsStateWithLifecycle()

            MainTheme(
                selectedTheme = appTheme,
                materialColorSchemes = SecondThemeColorScheme,
            ) {
                // Nothing may read app data until the migrations are over. Holding the splash
                // screen alone would not do: the content underneath still composes.
                if (migrationState is AppDataMigrationState.Finished) {
                    MainView(navController)

                    LaunchedEffect(Unit) {
                        extractDeeplink(intent)?.let {
                            logger.d { "Handle deeplink: $it" }
                            navController.navigate(it)
                        }
                    }
                }
            }
        }
    }
}
