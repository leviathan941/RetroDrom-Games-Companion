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

package org.leviathan941.retrodromcompanion.app

import android.app.Application
import com.mikepenz.aboutlibraries.Libs
import com.mikepenz.aboutlibraries.util.withJson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.leviathan941.retrodromcompanion.BuildConfig
import org.leviathan941.retrodromcompanion.MainActivity
import org.leviathan941.retrodromcompanion.R
import org.leviathan941.retrodromcompanion.app.di.AppGraph
import org.leviathan941.retrodromcompanion.app.licenses.AppLibrariesSource
import org.leviathan941.retrodromcompanion.firebase.push.MessagingDependencies
import org.leviathan941.retrodromcompanion.notification.Notifications
import org.leviathan941.retrodromcompanion.notification.PushMessaging
import org.leviathan941.retrodromcompanion.preferences.PreferencesRepository

class MainApplication :
    Application(),
    MessagingDependencies {
    private val sharedApplication: SharedApplication by lazy {
        SharedApplication(
            application = this,
            appVersionName = BuildConfig.VERSION_NAME,
            mainActivityClass = MainActivity::class.java,
            // The plugin generates R.raw.aboutlibraries from this module's classpath, the whole
            // app's. Referencing it by id also keeps it from the resource shrinker.
            appLibrariesSource = AppLibrariesSource {
                withContext(Dispatchers.IO) {
                    Libs.Builder().withJson(applicationContext, R.raw.aboutlibraries).build()
                }
            },
        )
    }

    val appGraph: AppGraph
        get() = sharedApplication.appGraph

    override val notifications: Notifications
        get() = appGraph.notifications

    override val pushMessaging: PushMessaging
        get() = appGraph.pushMessaging

    override val preferencesRepository: PreferencesRepository
        get() = appGraph.preferencesRepository

    override fun onCreate() {
        super.onCreate()
        sharedApplication.onCreate()
    }
}
