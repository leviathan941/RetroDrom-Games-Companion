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

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import kotlinx.coroutines.launch
import org.leviathan941.retrodromcompanion.rssreader.asDateTime
import org.leviathan941.retrodromcompanion.ui.RSS_SCREEN_TAG
import org.leviathan941.retrodromcompanion.ui.model.RssFeedViewModel
import org.leviathan941.retrodromcompanion.ui.model.RssFeedViewModelFactory
import org.leviathan941.retrodromcompanion.ui.model.ViewModelKeys
import org.leviathan941.retrodromcompanion.ui.navigation.MainNavScreen
import org.leviathan941.retrodromcompanion.ui.screen.feed.RssFeedItem
import org.leviathan941.retrodromcompanion.ui.screen.feed.RssFeedLoadFailedNextItem
import org.leviathan941.retrodromcompanion.ui.screen.feed.RssFeedLoadingNextItem
import org.leviathan941.retrodromcompanion.ui.screen.loading.LoadingState
import org.leviathan941.retrodromcompanion.ui.topbar.TopBarNavButton
import org.leviathan941.retrodromcompanion.ui.topbar.TopBarView

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RssFeedScreen(
    screen: MainNavScreen.RssFeed,
    drawerState: DrawerState,
    urlOpener: (url: String) -> Unit,
) {
    val screenViewModel: RssFeedViewModel = viewModel<RssFeedViewModel>(
        key = ViewModelKeys.RSS_FEED_VIEW_MODEL,
        factory = RssFeedViewModelFactory(screen.channelUrl),
    )
    val rssChannelItems = screenViewModel.rssChannelItems.collectAsLazyPagingItems()
    val clipboardManager = LocalClipboardManager.current
    val coroutineScope = rememberCoroutineScope()

    var isRefreshing by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopBarView(
                prefs = screen.topBarPrefs,
                onNavButtonCLick = { button ->
                    when (button) {
                        TopBarNavButton.DRAWER -> coroutineScope.launch {
                            drawerState.open()
                        }

                        else -> Unit
                    }
                }
            )
        },
    ) { paddings ->
        PullToRefreshBox(
            modifier = Modifier.padding(paddings),
            state = rememberPullToRefreshState(),
            onRefresh = {
                Log.d(RSS_SCREEN_TAG, "Refresh RSS channel: ${screen.channelUrl}")
                isRefreshing = true
                rssChannelItems.refresh()
            },
            isRefreshing = isRefreshing,
        ) {
            LazyColumn {
                items(rssChannelItems.itemCount) { index ->
                    val rssFeedItem = rssChannelItems[index]!!
                    RssFeedItem(
                        modifier = Modifier.clickable {
                            urlOpener(rssFeedItem.link)
                        },
                        title = rssFeedItem.title,
                        categories = rssFeedItem.categories,
                        pubDate = rssFeedItem.pubDate.asDateTime(),
                        imageUrl = rssFeedItem.description?.imageUrl,
                        description = rssFeedItem.description?.paragraphs?.firstOrNull(),
                    )
                    HorizontalDivider(
                        thickness = 1.dp,
                    )
                }

                rssChannelItems.apply {
                    when {
                        loadState.refresh is LoadState.Loading -> {
                            item {
                                LoadingView(
                                    modifier = Modifier.fillParentMaxSize(),
                                    state = LoadingState.InProgress,
                                )
                            }
                        }

                        loadState.refresh is LoadState.Error -> {
                            val error = loadState.refresh as LoadState.Error
                            item {
                                LoadingView(
                                    modifier = Modifier.fillParentMaxSize(),
                                    state = LoadingState.Failure(
                                        message = error.error.message.orEmpty(),
                                    ),
                                    onErrorLongPress = {
                                        clipboardManager.setText(it)
                                    },
                                    onRetryClick = { retry() }
                                )
                            }
                        }

                        loadState.append is LoadState.Loading -> {
                            item { RssFeedLoadingNextItem() }
                        }

                        loadState.append is LoadState.Error -> {
                            val error = loadState.append as LoadState.Error
                            item {
                                RssFeedLoadFailedNextItem(
                                    errorMessage = error.error.message
                                        ?: "Error during Rss feed loading",
                                    onRetry = { retry() },
                                    onErrorLongPress = {
                                        clipboardManager.setText(it)
                                    },
                                )
                            }
                        }
                    }
                }
            }

            LaunchedEffect(key1 = rssChannelItems.loadState) {
                when (rssChannelItems.loadState.refresh) {
                    is LoadState.Error,
                    is LoadState.NotLoading -> {
                        isRefreshing = false
                    }

                    is LoadState.Loading -> Unit
                }
            }
        }
    }
}
