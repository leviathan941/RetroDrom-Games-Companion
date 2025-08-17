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

import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.TextUnit
import androidx.core.text.HtmlCompat
import com.aghajari.compose.text.AnnotatedText
import com.aghajari.compose.text.fromHtml
import com.aghajari.compose.text.getInlineContentMap
import org.leviathan941.compose.htmltext.api.InlineContentCreator
import org.leviathan941.compose.htmltext.internal.createInlineContent
import org.leviathan941.compose.htmltext.internal.extractTags

@Composable
public fun HtmlText(
    html: String,
    linkColor: Color,
    modifier: Modifier = Modifier,
    textColor: Color = LocalContentColor.current,
    textStyle: TextStyle = MaterialTheme.typography.bodyLarge,
    inlineContentCreators: List<InlineContentCreator> = emptyList(),
    onLinkClick: (String) -> Unit = {},
) {
    val text = html.fromHtml(
        flags = HtmlCompat.FROM_HTML_MODE_LEGACY,
        linkColor = linkColor,
    )
    val htmlTags = extractTags(html)
    val inlineContent = inlineContentCreators.takeUnless { it.isEmpty() }?.let {
        text.createInlineContent(
            creators = it,
            tags = htmlTags,
        )
    } ?: text.getInlineContentMap()

    AnnotatedText(
        text = text,
        color = textColor,
        modifier = modifier,
        onURLClick = onLinkClick,
        // Workaround for https://issuetracker.google.com/issues/297002108
        style = textStyle.copy(lineHeight = TextUnit.Unspecified),
        inlineContent = inlineContent,
    )
}
