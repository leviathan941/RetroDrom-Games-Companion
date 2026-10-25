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

package org.leviathan941.compose.htmltext.internal

import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.LinkInteractionListener
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.em
import com.mohamedrejeb.ksoup.entities.KsoupEntities
import com.mohamedrejeb.ksoup.html.parser.KsoupHtmlHandler
import com.mohamedrejeb.ksoup.html.parser.KsoupHtmlOptions
import com.mohamedrejeb.ksoup.html.parser.KsoupHtmlParser
import org.leviathan941.compose.htmltext.api.HtmlTag
import org.leviathan941.compose.htmltext.api.InlineContentCreator

internal class HtmlContent(
    val text: AnnotatedString,
    val inlineContent: Map<String, InlineTextContent>,
)

/**
 * Converts [html] the way `android.text.Html`'s legacy mode laid it out: blocks are separated
 * by a blank line, list items by a line break, and runs of whitespace collapse to one space.
 */
internal fun htmlToContent(
    html: String,
    linkStyles: TextLinkStyles,
    linkInteractionListener: LinkInteractionListener?,
    inlineContentCreators: List<InlineContentCreator>,
): HtmlContent {
    val handler = ContentHandler(linkStyles, linkInteractionListener, inlineContentCreators)
    // ksoup-html 0.6.0 leaves numeric entities such as &#8217; undecoded, so the handler
    // decodes text and attributes itself.
    KsoupHtmlParser(handler = handler, options = KsoupHtmlOptions(decodeEntities = false)).apply {
        write(html)
        end()
    }
    return handler.build()
}

private val WHITESPACE = Regex("[ \t\n\r\u000C]+")

// Text inside these tags is not part of the rendered content.
private val SKIPPED_TAGS =
    setOf("head", "iframe", "noscript", "script", "style", "template", "title")

private val BLOCK_TAGS = setOf(
    "address", "article", "aside", "blockquote", "div", "dl", "dt", "dd", "figcaption",
    "figure", "footer", "header", "main", "nav", "ol", "p", "pre", "section", "table", "ul",
)

// android.text.Html's relative heading sizes.
private val HEADING_SIZES = mapOf(
    "h1" to 1.5f,
    "h2" to 1.4f,
    "h3" to 1.3f,
    "h4" to 1.2f,
    "h5" to 1.1f,
    "h6" to 1f,
)

private const val BLOCK_BREAK = 2
private const val LINE_BREAK = 1

private class ContentHandler(
    private val linkStyles: TextLinkStyles,
    private val linkInteractionListener: LinkInteractionListener?,
    private val inlineContentCreators: List<InlineContentCreator>,
) : KsoupHtmlHandler {
    private val builder = AnnotatedString.Builder()
    private val inlineContent = mutableMapOf<String, InlineTextContent>()

    // The SpanStyle or LinkAnnotation of each open tag, or null. They are pushed once content
    // follows, so none covers a deferred break or space; the first appliedStyles are pushed.
    private val openStyles = ArrayDeque<Any?>()
    private var appliedStyles = 0
    private var skipDepth = 0
    private var trailingNewlines = 0
    private var pendingNewlines = 0
    private var pendingSpace = false

    private val atLineStart: Boolean
        get() = builder.length == 0 || trailingNewlines > 0

    fun build(): HtmlContent = HtmlContent(builder.toAnnotatedString(), inlineContent.toMap())

    override fun onOpenTag(
        name: String,
        attributes: Map<String, String>,
        isImplied: Boolean,
    ) {
        if (skipDepth > 0 || name in SKIPPED_TAGS) {
            skipDepth++
            openStyles.addLast(null)
            return
        }

        val decodedAttributes = attributes.mapValues { KsoupEntities.decodeHtml(it.value) }
        inlineContentCreators.firstNotNullOfOrNull {
            it.create(HtmlTag(name, decodedAttributes, isImplied))
        }?.let { content ->
            val id = "html-inline-${inlineContent.size}"
            beforeContent()
            builder.appendInlineContent(id)
            inlineContent[id] = content
        }

        openStyles.addLast(openTag(name, decodedAttributes))
    }

    override fun onCloseTag(
        name: String,
        isImplied: Boolean,
    ) {
        val style = openStyles.removeLastOrNull()
        if (appliedStyles > openStyles.size) {
            appliedStyles--
            if (style != null) builder.pop()
        }
        when {
            skipDepth > 0 -> skipDepth--
            name in BLOCK_TAGS || name in HEADING_SIZES -> requestBreak(BLOCK_BREAK)
            name == "li" -> requestBreak(LINE_BREAK)
        }
    }

    override fun onText(text: String) {
        if (skipDepth > 0) return
        var collapsed = KsoupEntities.decodeHtml(text).replace(WHITESPACE, " ")
        if (collapsed.startsWith(' ')) {
            pendingSpace = true
            collapsed = collapsed.substring(1)
        }
        if (collapsed.isEmpty()) return

        val endsWithSpace = collapsed.endsWith(' ')
        beforeContent()
        builder.append(if (endsWithSpace) collapsed.dropLast(1) else collapsed)
        pendingSpace = endsWithSpace
    }

    /**
     * Starts [name]'s block or list item and returns its style, if it has one.
     */
    private fun openTag(
        name: String,
        attributes: Map<String, String>,
    ): Any? {
        if (name in BLOCK_TAGS || name in HEADING_SIZES) requestBreak(BLOCK_BREAK)
        return when (name) {
            "br" -> {
                beforeContent()
                appendNewline()
                null
            }

            "li" -> {
                requestBreak(LINE_BREAK)
                beforeContent()
                builder.append('•')
                pendingSpace = true
                null
            }

            "a" -> attributes["href"]?.takeIf { it.isNotBlank() }?.let { href ->
                LinkAnnotation.Url(href, linkStyles, linkInteractionListener)
            }

            else -> spanStyleOf(name)
        }
    }

    private fun requestBreak(newlines: Int) {
        pendingNewlines = maxOf(pendingNewlines, newlines)
        pendingSpace = false
    }

    // Breaks and spaces are deferred until content follows, so none trail the text.
    private fun beforeContent() {
        if (builder.length > 0) {
            repeat(pendingNewlines - trailingNewlines) { appendNewline() }
        }
        pendingNewlines = 0
        if (pendingSpace && !atLineStart) builder.append(' ')
        pendingSpace = false
        trailingNewlines = 0
        while (appliedStyles < openStyles.size) {
            when (val style = openStyles[appliedStyles++]) {
                is SpanStyle -> builder.pushStyle(style)
                is LinkAnnotation -> builder.pushLink(style)
            }
        }
    }

    private fun appendNewline() {
        builder.append('\n')
        trailingNewlines++
    }
}

private fun spanStyleOf(name: String): SpanStyle? = when (name) {
    "b", "strong" -> SpanStyle(fontWeight = FontWeight.Bold)
    "i", "em", "cite", "dfn" -> SpanStyle(fontStyle = FontStyle.Italic)
    "u", "ins" -> SpanStyle(textDecoration = TextDecoration.Underline)
    "s", "strike", "del" -> SpanStyle(textDecoration = TextDecoration.LineThrough)
    "big" -> SpanStyle(fontSize = 1.25.em)
    "small" -> SpanStyle(fontSize = 0.8.em)
    "sup" -> SpanStyle(baselineShift = BaselineShift.Superscript)
    "sub" -> SpanStyle(baselineShift = BaselineShift.Subscript)
    "tt", "code" -> SpanStyle(fontFamily = FontFamily.Monospace)
    else -> HEADING_SIZES[name]?.let { SpanStyle(fontWeight = FontWeight.Bold, fontSize = it.em) }
}
