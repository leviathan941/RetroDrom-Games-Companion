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

package org.leviathan941.compose.htmltext

import com.mohamedrejeb.ksoup.html.parser.KsoupHtmlHandler
import com.mohamedrejeb.ksoup.html.parser.KsoupHtmlParser

internal data class ImgTag(
    val src: String,
    val width: Int,
    val height: Int,
)

internal fun extractImgTags(html: String): Map<String, ImgTag> {
    return buildMap {
        KsoupHtmlParser(
            handler = object : KsoupHtmlHandler {
                override fun onOpenTag(
                    name: String,
                    attributes: Map<String, String>,
                    isImplied: Boolean
                ) {
                    if (name == "img") {
                        val src = attributes["src"] ?: return
                        val width = attributes["width"]?.toIntOrNull() ?: return
                        val height = attributes["height"]?.toIntOrNull() ?: return
                        put(src, ImgTag(src, width, height))
                    }
                }
            }
        ).apply {
            write(html)
            end()
        }
    }
}
