/*
 * RetroDrom Games Companion
 * Copyright (C) 2026 Alexey Kuzin <amkuzink@gmail.com>.
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

package org.leviathan941.retrodromcompanion.ui.navigation

import androidx.navigation3.runtime.NavKey
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Pins the back stack each action leaves to the one Nav2's `popUpTo` / `launchSingleTop` options
 * produced, so moving to an app-owned back stack changed no navigation behaviour.
 */
class MainNavActionsImplTest {
    private val startFeed = feed(id = 0)
    private val otherFeed = feed(id = 1)
    private val item = RssFeedDestination.ItemDescription(
        title = "Title",
        link = "https://example.com/post",
        pubDate = "Sat, 24 Oct 2026 10:00:00 +0000",
        html = "<p>Text</p>",
        creator = null,
    )
    private val loadingItem = RssFeedDestination.LoadingItem(postId = "1001")

    private fun actionsOn(vararg destinations: NavKey): Pair<MainNavActions, MutableList<NavKey>> {
        val backStack = destinations.toMutableList()
        return MainNavActionsImpl(backStack, MainNavPredefinedDestinations(startFeed)) to backStack
    }

    @Test
    fun `another feed replaces the current feed`() {
        val (actions, backStack) = actionsOn(startFeed)
        actions.navigateToRssFeed(otherFeed)
        assertEquals(listOf(otherFeed), backStack)
    }

    @Test
    fun `the current feed is not opened again`() {
        val (actions, backStack) = actionsOn(startFeed, otherFeed)
        actions.navigateToRssFeed(otherFeed)
        assertEquals(listOf(startFeed, otherFeed), backStack)
    }

    @Test
    fun `a feed opened from settings stays above the last feed`() {
        val (actions, backStack) = actionsOn(
            startFeed,
            SettingsDestination.Main,
            SettingsDestination.AppTheme,
        )
        actions.navigateToRssFeed(otherFeed)
        assertEquals(listOf(startFeed, otherFeed), backStack)
    }

    @Test
    fun `the last feed opened from settings is reused`() {
        val (actions, backStack) = actionsOn(startFeed, SettingsDestination.Main)
        actions.navigateToRssFeed(startFeed)
        assertEquals(listOf(startFeed), backStack)
    }

    @Test
    fun `an item replaces the loading screen above the feed`() {
        val (actions, backStack) = actionsOn(startFeed, loadingItem)
        actions.navigateToRssItemDescription(item)
        assertEquals(listOf(startFeed, item), backStack)
    }

    @Test
    fun `the open item is not opened again`() {
        val (actions, backStack) = actionsOn(startFeed, item)
        actions.navigateToRssItemDescription(item)
        assertEquals(listOf(startFeed, item), backStack)
    }

    @Test
    fun `the start feed replaces the loading screen`() {
        val (actions, backStack) = actionsOn(startFeed, loadingItem)
        actions.navigateToStartedRssFeed()
        assertEquals(listOf(startFeed), backStack)
    }

    @Test
    fun `settings and their items open single top`() {
        val (actions, backStack) = actionsOn(startFeed)
        actions.navigateToSettings()
        actions.navigateToSettings()
        actions.navigateToSettingsItem(SettingsDestination.Feedback)
        actions.navigateToSettingsItem(SettingsDestination.Feedback)
        assertEquals(
            listOf(startFeed, SettingsDestination.Main, SettingsDestination.Feedback),
            backStack,
        )
    }

    @Test
    fun `back pops the top destination`() {
        val (actions, backStack) = actionsOn(startFeed, SettingsDestination.Main)
        actions.navigateBack()
        assertEquals(listOf(startFeed), backStack)
    }

    @Test
    fun `back from the only destination opens the start feed`() {
        val (actions, backStack) = actionsOn(item)
        actions.navigateBack()
        assertEquals(listOf(item, startFeed), backStack)
    }

    private fun feed(id: Int) = RssFeedDestination.Feed(
        id = id,
        title = "Feed $id",
        channelUrl = "https://example.com/feed$id",
    )
}
