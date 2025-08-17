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

package org.leviathan941.compose.htmltext.internal

import androidx.compose.foundation.text.InlineTextContent
import com.aghajari.compose.text.ContentAnnotatedString
import com.mohamedrejeb.ksoup.html.parser.KsoupHtmlHandler
import com.mohamedrejeb.ksoup.html.parser.KsoupHtmlParser
import org.leviathan941.compose.htmltext.api.HtmlTag
import org.leviathan941.compose.htmltext.api.InlineContentCreator

internal fun ContentAnnotatedString.createInlineContent(
    creators: List<InlineContentCreator>,
    tags: List<HtmlTag>,
): Map<String, InlineTextContent> = inlineContents.mapNotNull { content ->
    creators.firstNotNullOfOrNull { mapper ->
        mapper.create(
            id = content.id,
            span = content.span,
            start = content.start,
            end = content.end,
            tags = tags,
        )
    }?.let {
        content.id to it
    }
}.toMap()

internal fun extractTags(html: String): List<HtmlTag> = buildList {
    KsoupHtmlParser(
        handler = object : KsoupHtmlHandler {
            override fun onOpenTag(
                name: String,
                attributes: Map<String, String>,
                isImplied: Boolean,
            ) {
                add(HtmlTag(name, attributes, isImplied))
            }
        },
    ).apply {
        write(html)
        end()
    }
}
