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

package org.leviathan941.retrodromcompanion.ui.screen.settings

import android.content.Context
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.cancellable
import kotlinx.coroutines.launch
import org.leviathan941.retrodromcompanion.R
import org.leviathan941.retrodromcompanion.app.Singletons
import org.leviathan941.retrodromcompanion.firebase.push.Messaging
import org.leviathan941.retrodromcompanion.ui.APP_THEME_DEFAULT
import org.leviathan941.retrodromcompanion.ui.theme.ThemeType

class SettingsViewModel : ViewModel() {
    private val _appTheme = MutableStateFlow(APP_THEME_DEFAULT)
    val appTheme: StateFlow<ThemeType> = _appTheme.asStateFlow()

    private val _subscribedPushTopics = MutableStateFlow(emptySet<Messaging.Topic>())
    val subscribedPushTopics: StateFlow<Set<Messaging.Topic>> = _subscribedPushTopics.asStateFlow()

    init {
        viewModelScope.launch {
            Singletons.preferencesRepository.ui
                .cancellable()
                .collect { uiPreferences ->
                    _appTheme.value = ThemeType.fromValue(uiPreferences.appTheme)
                    _subscribedPushTopics.value = uiPreferences.subscribedPushTopics
                        .mapNotNull { topicName ->
                            Messaging.topicFromValue(topicName)
                        }.toSet()
                }
        }
    }

    fun setAppTheme(appTheme: ThemeType) {
        viewModelScope.launch {
            Singletons.preferencesRepository.setAppTheme(appTheme.value)
        }
    }

    fun subscribeToTopic(
        context: Context,
        topic: Messaging.Topic,
    ) {
        viewModelScope.launch {
            if (Messaging.subscribeToTopic(topic)) {
                Singletons.preferencesRepository.addPushTopicSubscription(topic.value)
            } else {
                showTopicSubscriptionFailedToast(
                    context = context,
                    textRes = R.string.push_topic_subscription_failed_toast_text,
                    topicName = Messaging.Topic.NEW_RETRODROM_POSTS.value
                )
            }
        }
    }

    fun unsubscribeFromTopic(
        context: Context,
        topic: Messaging.Topic,
    ) {
        viewModelScope.launch {
            if (Messaging.unsubscribeFromTopic(Messaging.Topic.NEW_RETRODROM_POSTS)) {
                Singletons.preferencesRepository.removePushTopicSubscription(topic.value)
            } else {
                showTopicSubscriptionFailedToast(
                    context = context,
                    textRes = R.string.push_topic_unsubscription_failed_toast_text,
                    topicName = Messaging.Topic.NEW_RETRODROM_POSTS.value
                )
            }
        }
    }

    private fun showTopicSubscriptionFailedToast(
        context: Context,
        @StringRes textRes: Int,
        topicName: String,
    ) {
        Toast.makeText(
            context,
            context.getString(
                textRes,
                topicName
            ),
            Toast.LENGTH_SHORT
        ).show()
    }
}
