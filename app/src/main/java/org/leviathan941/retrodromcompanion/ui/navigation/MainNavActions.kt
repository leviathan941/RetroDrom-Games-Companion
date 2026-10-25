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

package org.leviathan941.retrodromcompanion.ui.navigation

import androidx.navigation3.runtime.NavKey
import org.leviathan941.retrodromcompanion.ui.mainViewLogger

data class MainNavPredefinedDestinations(
    val rssFeedStart: RssFeedDestination.Feed,
)

interface MainNavActions {
    fun navigateBack()
    fun navigateToStartedRssFeed()
    fun navigateToRssFeed(destination: RssFeedDestination.Feed)
    fun navigateToRssItemDescription(destination: RssFeedDestination.ItemDescription)
    fun navigateToSettings()
    fun navigateToSettingsItem(destination: SettingsDestination)
}

class MainNavActionsImpl(
    private val backStack: MutableList<NavKey>,
    private val predefinedDestinations: MainNavPredefinedDestinations,
) : MainNavActions {
    override fun navigateBack() {
        // The display needs at least one entry, so the last one is never popped.
        if (backStack.size > 1) {
            backStack.removeAt(backStack.lastIndex)
            mainViewLogger.d { "Navigate back" }
        } else {
            mainViewLogger.d { "No back stack entry, cannot navigate back" }
            navigateToStartedRssFeed()
        }
    }

    override fun navigateToStartedRssFeed() {
        navigateToRssFeed(predefinedDestinations.rssFeedStart)
    }

    override fun navigateToRssFeed(destination: RssFeedDestination.Feed) {
        mainViewLogger.d { "Navigate to RSS feed screen: $destination" }
        val currentDestination = backStack.lastOrNull()
        if (currentDestination == destination) {
            mainViewLogger.d { "Already on the same RSS feed screen" }
            return
        }
        popUpToLastFeed(inclusive = currentDestination is RssFeedDestination.Feed)
        pushSingleTop(destination)
    }

    override fun navigateToRssItemDescription(destination: RssFeedDestination.ItemDescription) {
        mainViewLogger.d { "Navigate to RSS item description screen" }
        popUpToLastFeed(inclusive = false)
        pushSingleTop(destination)
    }

    override fun navigateToSettings() {
        mainViewLogger.d { "Navigate to settings screen" }
        pushSingleTop(SettingsDestination.Main)
    }

    override fun navigateToSettingsItem(destination: SettingsDestination) {
        mainViewLogger.d { "Navigate to $destination settings screen" }
        pushSingleTop(destination)
    }

    /**
     * Pops every entry above the topmost feed, and that feed too when [inclusive].
     */
    private fun popUpToLastFeed(inclusive: Boolean) {
        val feedIndex = backStack.indexOfLast { it is RssFeedDestination.Feed }
        if (feedIndex < 0) return
        val firstPopped = if (inclusive) feedIndex else feedIndex + 1
        backStack.subList(firstPopped, backStack.size).clear()
    }

    private fun pushSingleTop(destination: AppDestination) {
        if (backStack.lastOrNull() != destination) {
            backStack.add(destination)
        }
    }
}

class MainNavActionsStub : MainNavActions {
    override fun navigateBack() = Unit
    override fun navigateToStartedRssFeed() = Unit
    override fun navigateToRssFeed(destination: RssFeedDestination.Feed) = Unit
    override fun navigateToRssItemDescription(destination: RssFeedDestination.ItemDescription) =
        Unit
    override fun navigateToSettings() = Unit
    override fun navigateToSettingsItem(destination: SettingsDestination) = Unit
}
