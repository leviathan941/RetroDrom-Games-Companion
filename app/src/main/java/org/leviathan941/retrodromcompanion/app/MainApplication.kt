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
import android.util.Log
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import org.leviathan941.retrodromcompanion.BuildConfig
import org.leviathan941.retrodromcompanion.MainActivity
import org.leviathan941.retrodromcompanion.firebase.push.Messaging
import org.leviathan941.retrodromcompanion.notification.Notifications

class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Singletons.init(application = this)
        Notifications.initialize(
            context = this,
            pushActivityClass = MainActivity::class.java,
        )

        if (BuildConfig.DEBUG) {
            onCreateDebug()
        }
    }

    private fun onCreateDebug() {
        ProcessLifecycleOwner.get().lifecycleScope.launch {
            Log.d(TAG, "Messaging token: ${Messaging.registrationToken()}")
        }
    }

    private companion object {
        private const val TAG = "MainApplication"
    }
}
