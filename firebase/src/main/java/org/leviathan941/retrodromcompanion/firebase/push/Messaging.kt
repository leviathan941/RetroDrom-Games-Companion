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

package org.leviathan941.retrodromcompanion.firebase.push

import android.util.Log
import com.google.firebase.messaging.FirebaseMessaging
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

public object Messaging {
    private const val TAG = "Messaging"

    public enum class Topic(
        public val value: String,
    ) {
        NEW_RETRODROM_POSTS("new_retrodrom_posts"),
    }

    public suspend fun registrationToken(): String {
        return suspendCoroutine { continuation ->
            FirebaseMessaging.getInstance().token
                .addOnSuccessListener { token ->
                    continuation.resume(token)
                }
                .addOnFailureListener { ex ->
                    Log.e(TAG, "Failed to get registration token", ex)
                    continuation.resumeWithException(ex)
                }
        }
    }

    public suspend fun subscribeToTopic(topic: Topic): Boolean {
        return suspendCoroutine { continuation ->
            FirebaseMessaging.getInstance().subscribeToTopic(topic.value)
                .addOnSuccessListener {
                    Log.d(TAG, "Subscribed to topic $topic")
                    continuation.resume(true)
                }
                .addOnFailureListener { ex ->
                    Log.e(TAG, "Failed to subscribe to topic $topic", ex)
                    continuation.resume(false)
                }
        }
    }

    public suspend fun unsubscribeFromTopic(topic: Topic): Boolean {
        return suspendCoroutine { continuation ->
            FirebaseMessaging.getInstance().unsubscribeFromTopic(topic.value)
                .addOnSuccessListener {
                    Log.d(TAG, "Unsubscribed from topic $topic")
                    continuation.resume(true)
                }
                .addOnFailureListener { ex ->
                    Log.e(TAG, "Failed to unsubscribe from topic $topic", ex)
                    continuation.resume(false)
                }
        }
    }

    public fun topicFromValue(value: String): Topic? {
        return Topic.entries.find { it.value == value }
    }
}