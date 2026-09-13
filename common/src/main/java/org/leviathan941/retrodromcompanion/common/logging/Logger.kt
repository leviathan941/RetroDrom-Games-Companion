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

package org.leviathan941.retrodromcompanion.common.logging

import co.touchlab.kermit.Logger as KermitLogger

/**
 * The logging facade of the app. Backed by Kermit, which writes to Logcat on Android and to
 * `NSLog`/`os_log` on Apple platforms, so no call site depends on a platform logging API.
 *
 * A tag belongs to a logger, never to a call site: obtain one with [withTag] and keep it as a
 * property of the component that logs.
 *
 * ```
 * private val logger = Logger.withTag("RssFeedItem")
 *
 * logger.d { "Loading $url" }
 * logger.e(error) { "Failed to load $url" }
 * ```
 *
 * Name it `logger` and keep it file-private. Only when several files genuinely share one tag
 * make it `internal val <scope>Logger`, so that a use site in another file still says which
 * logger it is.
 *
 * Messages are lambdas so that nothing is formatted when the severity is disabled.
 */
public class Logger private constructor(
    private val delegate: KermitLogger,
) {
    public fun v(throwable: Throwable? = null, message: () -> String): Unit =
        delegate.v(throwable, message = message)

    public fun d(throwable: Throwable? = null, message: () -> String): Unit =
        delegate.d(throwable, message = message)

    public fun i(throwable: Throwable? = null, message: () -> String): Unit =
        delegate.i(throwable, message = message)

    public fun w(throwable: Throwable? = null, message: () -> String): Unit =
        delegate.w(throwable, message = message)

    public fun e(throwable: Throwable? = null, message: () -> String): Unit =
        delegate.e(throwable, message = message)

    public companion object {
        public fun withTag(tag: String): Logger = Logger(KermitLogger.withTag(tag))

        /**
         * Drops every message below [level].
         *
         * Applies to every [Logger] in the process, including ones created before this call:
         * they all share one configuration, which is read again on each log call. The default
         * is [LogLevel.Verbose], which logs everything.
         */
        public fun setMinLevel(level: LogLevel): Unit =
            KermitLogger.setMinSeverity(level.toSeverity())
    }
}
