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

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshContainer
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import org.leviathan941.retrodromcompanion.ui.model.RssFeedViewModel
import org.leviathan941.retrodromcompanion.ui.model.ViewModelKeys
import org.leviathan941.retrodromcompanion.ui.navigation.MainNavScreen
import org.leviathan941.retrodromcompanion.ui.screen.feed.RssFeedShowContent
import org.leviathan941.retrodromcompanion.ui.screen.loading.LoadingState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RssFeedScreen(
    viewModelStoreOwner: ViewModelStoreOwner,
    screen: MainNavScreen.RssFeed,
    urlOpener: (url: String) -> Unit,
) {
    val screenViewModel: RssFeedViewModel = viewModel(
        viewModelStoreOwner = viewModelStoreOwner,
        key = ViewModelKeys.RSS_FEED_VIEW_MODEL,
    )
    val uiState by screenViewModel.uiState.collectAsState()
    val clipboardManager = LocalClipboardManager.current

    val pullToRefreshState = rememberPullToRefreshState(
        positionalThreshold = 96.dp,
    )

    Box(
        modifier = Modifier
            .nestedScroll(pullToRefreshState.nestedScrollConnection),
    ) {
        when (val loadingState = uiState.loadingState) {
            LoadingState.Success -> {
                val channel = uiState.rssChannel
                require(channel != null) {
                    "RssChannel must not be null to be shown"
                }
                RssFeedShowContent(
                    channel = channel,
                    urlOpener = urlOpener,
                )
            }

            LoadingState.InProgress -> {
                LoadingScreen(LoadingState.InProgress)
            }

            is LoadingState.Failure -> {
                LoadingScreen(
                    loadingState = loadingState,
                    onErrorLongPress = {
                        clipboardManager.setText(it)
                    },
                    onRetryClick = {
                        screenViewModel.loadChannel(screen.channelUrl)
                    }
                )
            }
        }

        LaunchedEffect(key1 = pullToRefreshState.isRefreshing) {
            if (pullToRefreshState.isRefreshing) {
                screenViewModel.refreshChannel(screen.channelUrl, showIsRefreshing = true)
            }
        }
        LaunchedEffect(key1 = uiState.isRefreshing) {
            if (uiState.isRefreshing) {
                pullToRefreshState.startRefresh()
            } else {
                pullToRefreshState.endRefresh()
            }
        }

        PullToRefreshContainer(
            modifier = Modifier
                .align(Alignment.TopCenter),
            state = pullToRefreshState,
        )
    }

    LaunchedEffect(key1 = screen) {
        if (!uiState.isRefreshing &&
            uiState.rssChannel?.link != screen.channelUrl
        ) {
            screenViewModel.loadChannel(screen.channelUrl)
        }
    }
}
