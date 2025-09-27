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

import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import org.leviathan941.retrodromcompanion.ui.navigation.MainNavScreen
import org.leviathan941.retrodromcompanion.ui.screen.loading.LoadingState
import org.leviathan941.retrodromcompanion.ui.theme.ClickableTextColor
import org.leviathan941.retrodromcompanion.ui.topbar.TopBarView

@Composable
fun LoadingScreen(
    loadingData: MainNavScreen.Loading,
    modifier: Modifier = Modifier,
    onErrorLongPress: (label: CharSequence, text: CharSequence) -> Unit = { _, _ -> },
    onRetryClick: () -> Unit = {},
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopBarView(
                prefs = loadingData.topBarPrefs,
            )
        },
    ) { paddings ->
        LoadingView(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddings),
            state = loadingData.state,
            onErrorLongPress = onErrorLongPress,
            onRetryClick = onRetryClick,
        )
    }
}

@Composable
fun LoadingView(
    state: LoadingState,
    modifier: Modifier = Modifier,
    onErrorLongPress: (label: CharSequence, text: CharSequence) -> Unit = { _, _ -> },
    onRetryClick: () -> Unit = {},
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(
            space = 20.dp,
            alignment = Alignment.CenterVertically,
        ),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        when (state) {
            LoadingState.InProgress -> InProgressScreen()
            is LoadingState.Failure -> FailureScreen(
                failureState = state,
                onErrorLongPress = onErrorLongPress,
                onRetryClick = onRetryClick,
            )

            else -> Unit
        }
    }
}

@Composable
@Suppress("MultipleEmitters")
private fun InProgressScreen() {
    CircularProgressIndicator()
    Text(
        text = stringResource(id = R.string.loading_screen_in_progress_message),
        style = MaterialTheme.typography.titleMedium,
    )
}

@Composable
@Suppress("MultipleEmitters")
private fun FailureScreen(
    failureState: LoadingState.Failure,
    onErrorLongPress: (label: CharSequence, text: CharSequence) -> Unit,
    onRetryClick: () -> Unit,
) {
    Text(
        modifier = Modifier
            .combinedClickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                enabled = true,
                onLongClick = {
                    onErrorLongPress(
                        failureState.clipboardLabel,
                        failureState.message,
                    )
                },
                onClick = {},
            ),
        text = stringResource(R.string.loading_screen_failure_title),
        textAlign = TextAlign.Center,
        style = MaterialTheme.typography.titleMedium,
        overflow = TextOverflow.Ellipsis,
        maxLines = 4,
    )
    Text(
        modifier = Modifier
            .clickable { onRetryClick() },
        text = AnnotatedString(
            text = stringResource(
                id = R.string.loading_screen_failure_retry_button,
            ).uppercase(),
            spanStyle = SpanStyle(
                color = ClickableTextColor,
            ),
        ),
        style = MaterialTheme.typography.titleMedium,
    )
}

@Preview(showBackground = true)
@Composable
private fun InProgressLoadingScreen() = LoadingScreen(
    loadingData = MainNavScreen.Loading(
        title = "Loading screen",
        state = LoadingState.InProgress,
    ),
)

@Preview(showBackground = true)
@Composable
private fun FailureLoadingScreen() = LoadingScreen(
    loadingData = MainNavScreen.Loading(
        title = "Loading screen",
        state = LoadingState.Failure(
            message = "Error message",
            clipboardLabel = "Error message copied to clipboard",
        ),
    ),
)
