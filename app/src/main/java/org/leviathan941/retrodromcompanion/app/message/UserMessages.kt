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

package org.leviathan941.retrodromcompanion.app.message

import androidx.annotation.StringRes
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow

/**
 * A short message for the user, shown by the single snackbar host at the root of the UI.
 */
sealed interface UserMessage {
    data class Text(val text: String) : UserMessage

    data class Resource(
        @param:StringRes val textRes: Int,
        val formatArgs: List<Any> = emptyList(),
    ) : UserMessage
}

/**
 * The app-wide queue of [UserMessage]s. Each message is delivered once; one posted while nothing
 * collects is delivered when collection resumes.
 */
@Inject
@SingleIn(AppScope::class)
class UserMessages {
    // A Channel rather than a SharedFlow, which would drop messages posted with no collector.
    private val channel = Channel<UserMessage>(Channel.BUFFERED)

    val messages: Flow<UserMessage> = channel.receiveAsFlow()

    fun show(message: UserMessage) {
        channel.trySend(message)
    }
}
