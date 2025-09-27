/*
 * RetroDrom Games Companion
 * Copyright (C) 2025 Alexey Kuzin <amkuzink@gmail.com>.
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

package org.leviathan941.retrodromcompanion.ui.screen.settings.subscreen

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.LineBreak
import androidx.compose.ui.text.withLink
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import org.leviathan941.retrodromcompanion.R
import org.leviathan941.retrodromcompanion.ui.FEEDBACK_URL
import org.leviathan941.retrodromcompanion.ui.screen.settings.SettingsTextItem

@Composable
fun FeedbackSettingsSubScreen() {
    val siteFeedback = stringResource(id = R.string.settings_about_feedback_screen_site)
    val linkSpanStyle = SpanStyle(MaterialTheme.colorScheme.primary)
    SettingsTextItem(
        text = buildAnnotatedString {
            withStyle(
                ParagraphStyle(
                    lineBreak = LineBreak.Heading,
                ),
            ) {
                append(siteFeedback)
                append("\n")
                withLink(
                    link = LinkAnnotation.Url(
                        url = FEEDBACK_URL,
                        styles = TextLinkStyles(style = linkSpanStyle),
                    ),
                ) {
                    append(FEEDBACK_URL)
                }
            }
        },
    )
}

@Preview(showBackground = true)
@Composable
private fun FeedbackSettingsSubScreenPreview() {
    FeedbackSettingsSubScreen()
}
