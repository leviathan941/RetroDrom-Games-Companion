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

package org.leviathan941.retrodromcompanion.ui.screen

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.ViewModelStoreOwner
import org.leviathan941.retrodromcompanion.R
import org.leviathan941.retrodromcompanion.ui.model.RssLoadingItemViewModel
import org.leviathan941.retrodromcompanion.ui.model.RssLoadingItemViewState
import org.leviathan941.retrodromcompanion.ui.model.ViewModelKeys
import org.leviathan941.retrodromcompanion.ui.navigation.MainNavActions
import org.leviathan941.retrodromcompanion.ui.navigation.RssFeedDestination
import org.leviathan941.retrodromcompanion.ui.screen.loading.LoadingState
import org.leviathan941.retrodromcompanion.ui.toDestination
import org.leviathan941.retrodromcompanion.ui.topbar.TopBarNavButton
import org.leviathan941.retrodromcompanion.ui.topbar.TopBarPrefs
import org.leviathan941.retrodromcompanion.ui.topbar.TopBarView

@Composable
fun RssLoadingItemScreen(
    item: RssFeedDestination.LoadingItem,
    viewModelStoreOwner: ViewModelStoreOwner,
    navActions: MainNavActions,
    modifier: Modifier = Modifier,
    viewModel: RssLoadingItemViewModel = hiltViewModel<
        RssLoadingItemViewModel,
        RssLoadingItemViewModel.Factory,
        >(
        viewModelStoreOwner = viewModelStoreOwner,
        key = ViewModelKeys.RSS_ITEM_LOADING_MODEL,
        creationCallback = { factory ->
            factory.create(item)
        },
    ),
) {
    val viewState = viewModel.viewState.collectAsState()

    when (val state = viewState.value) {
        is RssLoadingItemViewState.Loading -> {
            Scaffold(
                modifier = modifier,
                topBar = {
                    TopBarView(
                        prefs = TopBarPrefs(
                            title = stringResource(R.string.rss_feed_post_loading_item_title),
                            navButton = TopBarNavButton.BACK,
                        ),
                        onNavButtonClick = { navButton ->
                            when (navButton) {
                                TopBarNavButton.BACK -> {
                                    viewModel.cancel()
                                    navActions.navigateToRssFeed()
                                }

                                else -> Unit
                            }
                        },
                    )
                },
            ) { paddings ->
                LoadingView(
                    modifier = Modifier
                        .padding(paddings)
                        .fillMaxSize(),
                    state = LoadingState.InProgress,
                )
            }
        }

        is RssLoadingItemViewState.Error -> {
            navActions.navigateToRssFeed()
        }

        is RssLoadingItemViewState.Success -> {
            state.item.toDestination()?.let {
                navActions.navigateToRssItemDescription(it)
            } ?: run {
                navActions.navigateToRssFeed()
            }
        }
    }
}
