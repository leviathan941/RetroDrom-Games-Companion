/*
 * RetroDrom Games Companion
 * Copyright (C) 2026 Alexey Kuzin <amkuzink@gmail.com>.
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

package org.leviathan941.retrodromcompanion.app.ios

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.window.ComposeUIViewController
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.zacsweers.metrox.viewmodel.LocalMetroViewModelFactory
import org.leviathan941.retrodromcompanion.app.SharedApplication
import org.leviathan941.retrodromcompanion.app.migration.AppDataMigrationState
import org.leviathan941.retrodromcompanion.ui.AppUi
import platform.UIKit.UIViewController

/**
 * The view controller that shows the app's whole UI, built on [sharedApplication], the one the
 * iOS app built with [createSharedApplication] and started.
 */
// Named as Compose Multiplatform's iOS template names it, after the type the iOS app sees.
@Suppress("ktlint:standard:function-naming")
public fun MainViewController(sharedApplication: SharedApplication): UIViewController {
    val appGraph = sharedApplication.appGraph
    return ComposeUIViewController {
        CompositionLocalProvider(
            LocalMetroViewModelFactory provides appGraph.metroViewModelFactory,
            LocalUriHandler provides IosUriHandler,
        ) {
            val migrationState by appGraph.appDataMigrator.state.collectAsStateWithLifecycle()
            AppUi(
                // shortcut: no deep link until C3 brings push notifications to open one from.
                deepLink = null,
                isAppDataReady = migrationState is AppDataMigrationState.Finished,
            )
        }
    }
}
