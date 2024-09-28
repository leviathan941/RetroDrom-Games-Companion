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

import android.text.style.URLSpan
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.core.text.HtmlCompat

private const val URL_TAG = "URL"

@Composable
fun HtmlText(
    html: String,
    linkColor: Color,
    modifier: Modifier = Modifier,
    commonTextStyle: TextStyle? = null,
    onLinkClick: (String) -> Unit = {},
) {
    val annotatedText = remember(html) {
        val spanned = HtmlCompat.fromHtml(
            html,
            HtmlCompat.FROM_HTML_MODE_LEGACY,
        )
        buildAnnotatedString {
            val spanStyle = commonTextStyle?.toSpanStyle()

            spanStyle?.let {
                addStyle(
                    style = it,
                    start = 0,
                    end = spanned.length,
                )
            }

            append(spanned.toString())

            spanned.getSpans(0, spanned.length, URLSpan::class.java).forEach { span ->
                val start = spanned.getSpanStart(span)
                val end = spanned.getSpanEnd(span)
                addUrlSpanLink(
                    span = span,
                    start = start,
                    end = end,
                    spanStyle = spanStyle,
                    linkColor = linkColor,
                    onLinkClick = onLinkClick,
                )
            }
        }
    }

    Text(
        text = annotatedText,
        modifier = modifier,
    )
}

private fun AnnotatedString.Builder.addUrlSpanLink(
    span: URLSpan,
    start: Int,
    end: Int,
    spanStyle: SpanStyle?,
    linkColor: Color,
    onLinkClick: (String) -> Unit,
) {
    addLink(
        clickable = LinkAnnotation.Clickable(
            tag = URL_TAG,
            styles = spanStyle?.let {
                TextLinkStyles(
                    it.copy(
                        color = linkColor,
                        textDecoration = TextDecoration.Underline,
                    )
                )
            },
        ) {
            onLinkClick(span.url)
        },
        start = start,
        end = end,
    )
}
