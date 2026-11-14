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
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame
import org.leviathan941.compose.htmltext.api.HtmlTag
import org.leviathan941.compose.htmltext.api.InlineContentCreator

/**
 * Pins the layout `android.text.Html`'s legacy mode gave the article text, which
 * `AnnotatedText` used to render.
 */
class HtmlContentTest {

    private fun convert(
        html: String,
        creators: List<InlineContentCreator> = emptyList(),
    ): HtmlContent = htmlToContent(
        html = html,
        linkStyles = TextLinkStyles(),
        linkInteractionListener = null,
        inlineContentCreators = creators,
    )

    private fun AnnotatedString.styleOf(substring: String): List<SpanStyle> {
        val start = text.indexOf(substring)
        return spanStyles.filter { it.start == start && it.end == start + substring.length }
            .map { it.item }
    }

    @Test
    fun `paragraphs are separated by a blank line without one trailing`() {
        assertEquals(
            "First.\n\nSecond.",
            convert("<p>First.</p>\n<p>Second.</p>\n").text.text,
        )
    }

    @Test
    fun `whitespace collapses and entities are decoded`() {
        assertEquals(
            "Games That Weren\u2019t \u2026 a\u00A0b &#8217; \uD83D\uDE00",
            convert(
                "<p>  Games   That\n Weren&#8217;t &#8230;\ta&nbsp;b  &amp;#8217; &#x1F600;</p>",
            ).text.text,
        )
    }

    @Test
    fun `line breaks and nested blocks`() {
        assertEquals(
            "one\ntwo\n\nthree",
            convert("<div><p>one<br/>two</p></div><div><div>three</div></div>").text.text,
        )
    }

    @Test
    fun `list items are bulleted on their own lines`() {
        assertEquals(
            "Intro\n\n• one\n• two\n\nAfter",
            convert("<p>Intro</p><ul>\n<li> one </li>\n<li>two</li></ul><p>After</p>").text.text,
        )
    }

    @Test
    fun `an empty list leaves no blank lines`() {
        assertEquals(
            "a\n\nb",
            convert("<p>a</p><div><ul>\n\t\t</ul>\n</p></div><p>b</p>").text.text,
        )
    }

    @Test
    fun `inline styles and headings`() {
        val text = convert("<h5>Head</h5><p><strong>bold</strong> and <em>it</em></p>").text

        assertEquals("Head\n\nbold and it", text.text)
        assertEquals(
            listOf(SpanStyle(fontWeight = FontWeight.Bold, fontSize = 1.1f.em)),
            text.styleOf("Head"),
        )
        assertEquals(listOf(SpanStyle(fontWeight = FontWeight.Bold)), text.styleOf("bold"))
        assertEquals(listOf(SpanStyle(fontStyle = FontStyle.Italic)), text.styleOf("it"))
    }

    @Test
    fun `links carry their url`() {
        val text = convert(
            "<p>Read <a href=\"https://retrodrom.games/news/post/\">the post</a> <a>here</a></p>",
        ).text
        val links = text.getLinkAnnotations(0, text.length)

        assertEquals("Read the post here", text.text)
        assertEquals(1, links.size)
        assertEquals(
            "https://retrodrom.games/news/post/",
            (links[0].item as LinkAnnotation.Url).url,
        )
        assertEquals(text.text.indexOf("the post"), links[0].start)
        assertEquals(text.text.indexOf(" here"), links[0].end)
    }

    @Test
    fun `scripts styles and iframes are skipped`() {
        assertEquals(
            "a\n\nb",
            convert(
                "<p>a</p><script>var x = 1;</script><style>p {}</style>" +
                    "<p><iframe src=\"https://www.youtube.com/embed/x\"></iframe></p><p>b</p>",
            ).text.text,
        )
    }

    @Test
    fun `creators receive each tag and their content is placed inline`() {
        val content =
            InlineTextContent(Placeholder(1.sp, 1.sp, PlaceholderVerticalAlign.TextBottom)) {}
        val offered = mutableListOf<HtmlTag>()
        val creator = object : InlineContentCreator {
            override fun create(tag: HtmlTag): InlineTextContent? {
                offered += tag
                return content.takeIf { tag.name == "img" }
            }
        }

        val result = convert(
            "<a href=\"https://example.com/\">" +
                "<img src=\"https://example.com/a.jpg?w=1&amp;h=2\"/></a>" +
                "<p>Text <IMG SRC=\"https://example.com/b.jpg\"> end</p>",
            listOf(creator),
        )

        assertEquals(listOf("a", "img", "p", "img"), offered.map { it.name })
        assertEquals("https://example.com/a.jpg?w=1&h=2", offered[1].attributes["src"])
        assertEquals(
            mapOf("src" to "https://example.com/b.jpg"),
            offered[3].attributes,
        )
        assertEquals(setOf("html-inline-0", "html-inline-1"), result.inlineContent.keys)
        result.inlineContent.values.forEach { assertSame(content, it) }

        val placeholders = result.text.getStringAnnotations(0, result.text.length)
        assertEquals(listOf("html-inline-0", "html-inline-1"), placeholders.map { it.item })
        assertEquals("\uFFFD\n\nText \uFFFD end", result.text.text)
    }
}
