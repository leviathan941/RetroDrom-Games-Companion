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

package org.leviathan941.retrodromcompanion.ui.screen.settings

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun SettingsTextItem(
    text: AnnotatedString,
    modifier: Modifier = Modifier,
    onAnnotationClick: (tag: String, annotation: String) -> Unit = { _, _ -> },
) {
    val onClick = { offset: Int ->
        text.getStringAnnotations(offset, offset).firstOrNull()?.let {
            onAnnotationClick(it.tag, it.item)
        }
    }
    val layoutResult = remember { mutableStateOf<TextLayoutResult?>(null) }
    val pressIndicator = Modifier.pointerInput(onClick) {
        detectTapGestures { pos ->
            layoutResult.value?.let { layoutResult ->
                onClick(layoutResult.getOffsetForPosition(pos))
            }
        }
    }

    Text(
        modifier = modifier
            .fillMaxWidth()
            .padding(all = 16.dp)
            .then(pressIndicator),
        text = text,
        style = MaterialTheme.typography.titleLarge,
        onTextLayout = {
            layoutResult.value = it
        },
    )
}

@Preview(showBackground = true)
@Composable
private fun SettingsTextItemPreview() {
    SettingsTextItem(
        text = buildAnnotatedString {
            append("This is a text item for ")

            pushStyle(
                SpanStyle(
                    MaterialTheme.colorScheme.primary,
                ),
            )
            append("checking the preview")
            pop()

            append(" of the text item.")
        },
    )
}
