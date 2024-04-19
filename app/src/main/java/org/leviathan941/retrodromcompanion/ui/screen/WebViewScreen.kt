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

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.kevinnzou.web.LoadingState
import com.kevinnzou.web.WebView
import com.kevinnzou.web.rememberWebViewState
import org.leviathan941.retrodromcompanion.ui.MAIN_VIEW_TAG

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun WebViewScreen(url: String) {
    val state = rememberWebViewState(url)
    val loadingState = state.loadingState

    Column {
        if (loadingState is LoadingState.Loading) {
            Log.d(MAIN_VIEW_TAG, "WebView progress: ${loadingState.progress}")
            LinearProgressIndicator(
                modifier = Modifier.fillMaxWidth(),
                progress = { loadingState.progress },
            )
        }

        // Workaround to make WebView scroll working is to put it inside LazyColumn
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
        ) {
            item {
                WebView(
                    state = state,
                    onCreated = { webView ->
                        webView.settings.run {
                            javaScriptEnabled = true
                        }
                    },
                )
            }
        }
    }
}
