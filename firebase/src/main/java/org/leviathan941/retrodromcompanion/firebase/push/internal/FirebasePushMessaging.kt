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

package org.leviathan941.retrodromcompanion.firebase.push.internal

import com.google.firebase.messaging.FirebaseMessaging
import dev.zacsweers.metro.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import org.leviathan941.retrodromcompanion.common.logging.Logger
import org.leviathan941.retrodromcompanion.notification.PushMessaging

@Inject
internal class FirebasePushMessaging : PushMessaging {
    private val logger = Logger.withTag("FirebasePushMessaging")

    override suspend fun subscribeToTopic(topic: PushMessaging.Topic): Boolean =
        suspendCoroutine { continuation ->
            FirebaseMessaging.getInstance().subscribeToTopic(topic.value)
                .addOnSuccessListener {
                    logger.d { "Subscribed to topic $topic" }
                    continuation.resume(true)
                }
                .addOnFailureListener { ex ->
                    logger.e(ex) { "Failed to subscribe to topic $topic" }
                    continuation.resume(false)
                }
        }

    override suspend fun unsubscribeFromTopic(topic: PushMessaging.Topic): Boolean =
        suspendCoroutine { continuation ->
            FirebaseMessaging.getInstance().unsubscribeFromTopic(topic.value)
                .addOnSuccessListener {
                    logger.d { "Unsubscribed from topic $topic" }
                    continuation.resume(true)
                }
                .addOnFailureListener { ex ->
                    logger.e(ex) { "Failed to unsubscribe from topic $topic" }
                    continuation.resume(false)
                }
        }
}
