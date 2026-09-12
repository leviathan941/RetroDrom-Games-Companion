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

import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import kotlinx.coroutines.launch
import org.leviathan941.retrodromcompanion.R
import org.leviathan941.retrodromcompanion.ui.FEEDBACK_EMAIL
import org.leviathan941.retrodromcompanion.ui.FEEDBACK_URL
import org.leviathan941.retrodromcompanion.ui.copyToClipboard
import org.leviathan941.retrodromcompanion.ui.openEmailByIntent
import org.leviathan941.retrodromcompanion.ui.openUrlByIntent
import org.leviathan941.retrodromcompanion.ui.screen.settings.SettingsClickableItem
import org.leviathan941.retrodromcompanion.ui.screen.settings.SettingsGroup

@Composable
fun FeedbackSettingsSubScreen() {
    val context = LocalContext.current
    val clipboard = LocalClipboard.current
    val coroutineScope = rememberCoroutineScope()
    val siteCopiedLabel = stringResource(
        id = R.string.settings_about_feedback_screen_site_copied_clipboard_label,
    )
    val emailCopiedLabel = stringResource(
        id = R.string.settings_about_feedback_screen_email_copied_clipboard_label,
    )

    SettingsGroup(
        title = stringResource(id = R.string.settings_about_feedback_screen_description),
        titleStyle = MaterialTheme.typography.bodyLarge,
    ) {
        SettingsClickableItem(
            title = stringResource(id = R.string.settings_about_feedback_screen_site_title),
            subtitle = FEEDBACK_URL,
            leadingIcon = painterResource(id = R.drawable.google_material_globe),
            onLongClick = {
                coroutineScope.launch {
                    copyToClipboard(context, clipboard, siteCopiedLabel, FEEDBACK_URL)
                }
            },
            onClick = {
                openUrlByIntent(context, FEEDBACK_URL)
            },
        )

        HorizontalDivider()

        SettingsClickableItem(
            title = stringResource(id = R.string.settings_about_feedback_screen_email_title),
            subtitle = FEEDBACK_EMAIL,
            leadingIcon = rememberVectorPainter(Icons.Default.Email),
            onLongClick = {
                coroutineScope.launch {
                    copyToClipboard(context, clipboard, emailCopiedLabel, FEEDBACK_EMAIL)
                }
            },
            onClick = {
                openEmailByIntent(context, FEEDBACK_EMAIL)
            },
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun FeedbackSettingsSubScreenPreview() {
    Column {
        FeedbackSettingsSubScreen()
    }
}
