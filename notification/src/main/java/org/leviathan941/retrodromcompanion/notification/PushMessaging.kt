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

package org.leviathan941.retrodromcompanion.notification

/**
 * Manages this device's subscriptions to push notification topics.
 */
public interface PushMessaging {
    /**
     * A topic the device can subscribe to. [value] is the topic's stable name on the push service.
     */
    public enum class Topic(
        public val value: String,
    ) {
        NEW_RETRODROM_POSTS("new_retrodrom_posts"),
        ;

        public companion object {
            /**
             * Returns the topic named [value], or `null` when there is no such topic.
             */
            public fun fromValue(value: String): Topic? = entries.find { it.value == value }
        }
    }

    /**
     * Subscribes this device to [topic]. Returns `true` once the push service has confirmed
     * the subscription, `false` when it failed.
     */
    public suspend fun subscribeToTopic(topic: Topic): Boolean

    /**
     * Unsubscribes this device from [topic]. Returns `true` once the push service has confirmed
     * the removal, `false` when it failed.
     */
    public suspend fun unsubscribeFromTopic(topic: Topic): Boolean
}
