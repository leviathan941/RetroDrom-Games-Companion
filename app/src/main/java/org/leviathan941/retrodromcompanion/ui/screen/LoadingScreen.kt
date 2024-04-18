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

package org.leviathan941.retrodromcompanion.ui.screen

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.leviathan941.retrodromcompanion.R
import org.leviathan941.retrodromcompanion.ui.screen.loading.LoadingState
import org.leviathan941.retrodromcompanion.ui.theme.RefreshClickableTextColor

@Composable
fun LoadingScreen(
    loadingState: LoadingState,
    onErrorLongPress: (text: AnnotatedString) -> Unit = {},
    onRetryClick: () -> Unit = {},
) {
    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(
            space = 20.dp,
            alignment = Alignment.CenterVertically,
        ),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        when (loadingState) {
            LoadingState.InProgress -> InProgressScreen()
            is LoadingState.Failure -> FailureScreen(
                message = loadingState.message,
                onErrorLongPress = onErrorLongPress,
                onRetryClick = onRetryClick,
            )

            else -> Unit
        }
    }
}

@Composable
private fun InProgressScreen() {
    CircularProgressIndicator()
    Text(
        text = stringResource(id = R.string.loading_screen_in_progress_message),
        style = MaterialTheme.typography.titleMedium,
    )
}

@Preview(showBackground = true)
@Composable
private fun InProgressLoadingScreen() = LoadingScreen(
    loadingState = LoadingState.InProgress,
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun FailureScreen(
    message: String,
    onErrorLongPress: (text: AnnotatedString) -> Unit,
    onRetryClick: () -> Unit,
) {
    Text(
        modifier = Modifier
            .combinedClickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                enabled = true,
                onLongClick = { onErrorLongPress(AnnotatedString(text = message)) },
                onClick = {},
            ),
        text = stringResource(R.string.loading_screen_failure_title),
        textAlign = TextAlign.Center,
        style = MaterialTheme.typography.titleMedium,
        overflow = TextOverflow.Ellipsis,
        maxLines = 2,
    )
    ClickableText(
        text = AnnotatedString(
            text = stringResource(
                id = R.string.loading_screen_failure_retry_button
            ).uppercase(),
            spanStyle = SpanStyle(
                color = RefreshClickableTextColor,
            ),
        ),
        onClick = { onRetryClick() },
        style = MaterialTheme.typography.titleMedium,
    )
}

@Preview(showBackground = true)
@Composable
private fun FailureLoadingScreen() = LoadingScreen(
    loadingState = LoadingState.Failure(
        message = "Error message",
    ),
)
