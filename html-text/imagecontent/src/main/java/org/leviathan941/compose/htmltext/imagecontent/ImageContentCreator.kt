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

import android.os.Build
import android.text.style.DynamicDrawableSpan
import android.text.style.ImageSpan
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import org.leviathan941.compose.htmltext.api.HtmlTag
import org.leviathan941.compose.htmltext.api.InlineContentCreator
import org.leviathan941.compose.htmltext.imagecontent.internal.findImgTag

public class ImageContentCreator(
    private val localDensity: Density,
    private val modifier: Modifier = Modifier,
    private val defaultWidth: TextUnit = 200.sp,
    private val defaultHeight: TextUnit = 234.sp,
    private val placeholder: Painter? = null,
    private val error: Painter? = null,
) : InlineContentCreator {
    override fun create(
        id: String,
        span: Any,
        start: Int,
        end: Int,
        tags: List<HtmlTag>,
    ): InlineTextContent? {
        val imageSpan = span as? ImageSpan ?: return null
        val imgTag = imageSpan.source?.let { tags.findImgTag(source = it) }
        val width = imgTag?.width.toSpOrElse(localDensity, defaultWidth)
        val height = imgTag?.height.toSpOrElse(localDensity, defaultHeight)

        return InlineTextContent(
            placeholder = Placeholder(
                width = width,
                height = height,
                placeholderVerticalAlign = when (imageSpan.verticalAlignment) {
                    DynamicDrawableSpan.ALIGN_BOTTOM -> {
                        PlaceholderVerticalAlign.TextBottom
                    }

                    DynamicDrawableSpan.ALIGN_CENTER -> {
                        PlaceholderVerticalAlign.TextCenter
                    }

                    DynamicDrawableSpan.ALIGN_BASELINE -> {
                        PlaceholderVerticalAlign.AboveBaseline
                    }

                    else -> {
                        PlaceholderVerticalAlign.TextTop
                    }
                }
            )
        ) {
            val desc = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                imageSpan.contentDescription?.toString()
            } else {
                null
            }

            AsyncImage(
                model = span.source,
                contentDescription = desc,
                modifier = modifier,
                placeholder = placeholder,
                error = error,
            )
        }
    }
}

private fun Int?.toSpOrElse(density: Density, default: TextUnit): TextUnit {
    return this?.takeIf { it >= 0 }?.let {
        with(density) { it.toSp() }
    } ?: default
}
