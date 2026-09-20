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

package org.leviathan941.retrodromcompanion.firebase.push

import org.leviathan941.retrodromcompanion.notification.Notifications

/**
 * What [MessagingService] needs from the application graph.
 *
 * `FirebaseMessagingService` is instantiated by the framework, so it cannot be constructor
 * injected. The `Application` implements this interface and delegates to the graph.
 */
public interface MessagingDependencies {
    public val notifications: Notifications
}
