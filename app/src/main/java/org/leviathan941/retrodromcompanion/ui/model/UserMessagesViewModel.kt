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

package org.leviathan941.retrodromcompanion.ui.model

import androidx.lifecycle.ViewModel
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.binding
import dev.zacsweers.metrox.viewmodel.ViewModelKey
import kotlinx.coroutines.flow.Flow
import org.leviathan941.retrodromcompanion.app.message.UserMessage
import org.leviathan941.retrodromcompanion.app.message.UserMessages

/**
 * Gives any screen access to the app-wide [UserMessages]. Every instance shares the same queue,
 * so [show] may be called from anywhere.
 */
@Inject
@ViewModelKey
@ContributesIntoMap(AppScope::class, binding<ViewModel>())
class UserMessagesViewModel(
    private val userMessages: UserMessages,
) : ViewModel() {
    /**
     * Each message is delivered to exactly one collector, so the UI must collect this in one
     * place only — the snackbar host at its root.
     */
    val messages: Flow<UserMessage>
        get() = userMessages.messages

    fun show(message: UserMessage) {
        userMessages.show(message)
    }
}
