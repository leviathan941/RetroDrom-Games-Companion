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

package org.leviathan941.retrodromcompanion.network.cache.internal.room.feed

import androidx.room3.ColumnTypeConverter
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList

/** Categories are free-form text, so they are joined with a separator that cannot occur in one. */
private const val CATEGORIES_SEPARATOR = "\u001F"

internal object RoomFeedTypeConverters {
    @ColumnTypeConverter
    fun categoriesToString(categories: ImmutableList<String>): String =
        categories.joinToString(separator = CATEGORIES_SEPARATOR)

    @ColumnTypeConverter
    fun stringToCategories(value: String): ImmutableList<String> =
        value.takeIf { it.isNotEmpty() }
            ?.split(CATEGORIES_SEPARATOR)
            ?.toImmutableList()
            ?: persistentListOf()
}
