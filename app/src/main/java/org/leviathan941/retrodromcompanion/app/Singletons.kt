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
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.lifecycle.lifecycleScope
import org.leviathan941.retrodromcompanion.app.model.PushNotificationModel
import org.leviathan941.retrodromcompanion.preferences.Preferences.mainDataStore
import org.leviathan941.retrodromcompanion.preferences.PreferencesRepository

object Singletons {
    lateinit var preferencesRepository: PreferencesRepository
    lateinit var pushNotificationModel: PushNotificationModel

    fun init(application: Application) {
        preferencesRepository = PreferencesRepository(application.applicationContext.mainDataStore)
        pushNotificationModel = PushNotificationModel(
            application = application,
            scope = ProcessLifecycleOwner.get().lifecycleScope,
            preferencesRepository = preferencesRepository,
        )
    }
}
