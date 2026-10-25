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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.LinkInteractionListener
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.TextUnit
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import org.leviathan941.compose.htmltext.api.InlineContentCreator
import org.leviathan941.compose.htmltext.internal.htmlToContent

@Composable
public fun HtmlText(
    html: String,
    linkColor: Color,
    modifier: Modifier = Modifier,
    textColor: Color = LocalContentColor.current,
    textStyle: TextStyle = MaterialTheme.typography.bodyLarge,
    inlineContentCreators: ImmutableList<InlineContentCreator> = persistentListOf(),
    onLinkClick: (String) -> Unit = {},
) {
    val currentOnLinkClick by rememberUpdatedState(onLinkClick)
    val content = remember(html, linkColor, inlineContentCreators) {
        htmlToContent(
            html = html,
            linkStyles = TextLinkStyles(
                style = SpanStyle(color = linkColor, textDecoration = TextDecoration.Underline),
            ),
            linkInteractionListener = { link ->
                (link as? LinkAnnotation.Url)?.let { currentOnLinkClick(it.url) }
            },
            inlineContentCreators = inlineContentCreators,
        )
    }

    Text(
        text = content.text,
        color = textColor,
        modifier = modifier,
        // Workaround for https://issuetracker.google.com/issues/297002108
        style = textStyle.copy(lineHeight = TextUnit.Unspecified),
        inlineContent = content.inlineContent,
    )
}
