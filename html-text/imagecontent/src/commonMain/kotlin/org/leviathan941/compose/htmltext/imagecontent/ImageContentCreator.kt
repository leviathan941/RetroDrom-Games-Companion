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

package org.leviathan941.compose.htmltext.imagecontent

import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import co.touchlab.kermit.Logger
import coil3.compose.AsyncImage
import org.leviathan941.compose.htmltext.api.HtmlTag
import org.leviathan941.compose.htmltext.api.InlineContentCreator
import org.leviathan941.compose.htmltext.imagecontent.internal.toImgTag

private val logger = Logger.withTag("HtmlTextImageContent")

public class ImageContentCreator(
    private val localDensity: Density,
    private val modifier: Modifier = Modifier,
    private val defaultWidth: TextUnit = 200.sp,
    private val defaultHeight: TextUnit = 234.sp,
    private val placeholder: Painter? = null,
    private val error: Painter? = null,
) : InlineContentCreator {
    override fun create(tag: HtmlTag): InlineTextContent? {
        if (tag.name != "img") return null
        val source = tag.attributes["src"] ?: return null
        val imgTag = tag.toImgTag()
        val width = imgTag?.width.toSpOrElse(localDensity, defaultWidth)
        val height = imgTag?.height.toSpOrElse(localDensity, defaultHeight)

        return InlineTextContent(
            placeholder = Placeholder(
                width = width,
                height = height,
                // android.text.Html aligned its image spans to the bottom.
                placeholderVerticalAlign = PlaceholderVerticalAlign.TextBottom,
            ),
        ) {
            AsyncImage(
                model = source,
                contentDescription = tag.attributes["alt"]?.takeIf { it.isNotBlank() },
                modifier = modifier,
                placeholder = placeholder,
                error = error,
                onError = { error ->
                    logger.e(error.result.throwable) { "Error while loading image" }
                },
            )
        }
    }
}

private fun Int?.toSpOrElse(
    density: Density,
    default: TextUnit,
): TextUnit = this?.takeIf {
    it >= 0
}?.let {
    with(density) { it.toSp() }
} ?: default
