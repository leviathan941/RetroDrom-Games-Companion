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
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.zacsweers.metrox.viewmodel.LocalMetroViewModelFactory
import org.leviathan941.retrodromcompanion.app.MainApplication
import org.leviathan941.retrodromcompanion.app.di.AppGraph
import org.leviathan941.retrodromcompanion.app.migration.AppDataMigrationState
import org.leviathan941.retrodromcompanion.app.migration.AppDataMigrator
import org.leviathan941.retrodromcompanion.notification.POST_ID_PAYLOAD_DATA_KEY
import org.leviathan941.retrodromcompanion.notification.extractDeeplink
import org.leviathan941.retrodromcompanion.ui.AppUi

class MainActivity : ComponentActivity() {
    private val appGraph: AppGraph
        get() = (application as MainApplication).appGraph

    private val appDataMigrator: AppDataMigrator
        get() = appGraph.appDataMigrator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen().setKeepOnScreenCondition {
            appDataMigrator.state.value !is AppDataMigrationState.Finished
        }
        val deepLink = extractDeeplink(
            data = intent.dataString,
            postId = intent.getStringExtra(POST_ID_PAYLOAD_DATA_KEY),
        )
        setContent {
            CompositionLocalProvider(
                LocalMetroViewModelFactory provides appGraph.metroViewModelFactory,
            ) {
                val migrationState by appDataMigrator.state.collectAsStateWithLifecycle()
                AppUi(
                    deepLink = deepLink,
                    isAppDataReady = migrationState is AppDataMigrationState.Finished,
                )
            }
        }
    }
}
