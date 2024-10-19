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

import android.os.Build
import android.text.style.DynamicDrawableSpan
import android.text.style.ImageSpan
import android.util.Log
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.TextUnit
import coil.compose.AsyncImage
import com.aghajari.compose.text.ContentAnnotatedString
import com.aghajari.compose.text.InlineContent

internal fun ContentAnnotatedString.coilInlineContent(
    localDensity: Density,
    defaultWidth: TextUnit,
    defaultHeight: TextUnit,
    imgTags: Map<String, ImgTag> = emptyMap(),
    placeholder: Painter? = null,
    error: Painter? = null,
): Map<String, InlineTextContent> {
    return inlineContents.mapNotNull { content ->
        coilImageInlineContentCreator(
            content = content,
            localDensity = localDensity,
            imgTags = imgTags,
            defaultWidth = defaultWidth,
            defaultHeight = defaultHeight,
            placeholder = placeholder,
            error = error,
        )?.let {
            content.id to it
        }
    }.toMap()
}

private fun coilImageInlineContentCreator(
    content: InlineContent,
    localDensity: Density,
    imgTags: Map<String, ImgTag>,
    defaultWidth: TextUnit,
    defaultHeight: TextUnit,
    placeholder: Painter?,
    error: Painter?,
): InlineTextContent? {
    val span = content.span as? ImageSpan ?: return null
    val imgTag = span.source?.let { imgTags[it] }
    val width = imgTag?.width.toSpOrElse(localDensity, defaultWidth)
    val height = imgTag?.height.toSpOrElse(localDensity, defaultHeight)
    Log.d(
        LOG_TAG,
        "InlineImageCreator: ${span.source}, width: $width, height: $height"
    )

    return InlineTextContent(
        placeholder = Placeholder(
            width = width,
            height = height,
            placeholderVerticalAlign = when (span.verticalAlignment) {
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
            span.contentDescription?.toString()
        } else {
            null
        }

        AsyncImage(
            model = span.source,
            contentDescription = desc,
            modifier = Modifier.fillMaxSize(),
            placeholder = placeholder,
            error = error,
        )
    }
}

private fun Int?.toSpOrElse(density: Density, default: TextUnit): TextUnit {
    return this?.takeIf { it >= 0 }?.let {
        with(density) { it.toSp() }
    } ?: default
}
