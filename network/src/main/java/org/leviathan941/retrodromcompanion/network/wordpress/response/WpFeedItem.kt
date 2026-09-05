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

package org.leviathan941.retrodromcompanion.network.wordpress.response

import kotlinx.serialization.Serializable
import nl.adaptivity.xmlutil.serialization.XmlElement
import nl.adaptivity.xmlutil.serialization.XmlSerialName

@Serializable
@XmlSerialName(value = "item")
public data class WpFeedItem(
    @XmlElement
    val title: String? = null,
    @XmlElement
    val link: String? = null,
    @XmlElement
    val pubDate: String? = null,
    @XmlSerialName(value = "category")
    @XmlElement
    val categories: List<String> = emptyList(),
    @XmlSerialName(value = "creator", namespace = DC_NAMESPACE, prefix = "dc")
    @XmlElement
    val creator: String? = null,
    @XmlElement
    val description: String? = null,
    @XmlSerialName(value = "post_id")
    @XmlElement
    val postId: String? = null,
)

private const val DC_NAMESPACE = "http://purl.org/dc/elements/1.1/"
