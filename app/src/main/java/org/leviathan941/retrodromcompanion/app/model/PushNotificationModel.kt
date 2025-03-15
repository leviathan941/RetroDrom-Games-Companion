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

package org.leviathan941.retrodromcompanion.app.model

import android.app.Application
import android.widget.Toast
import androidx.annotation.StringRes
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.cancellable
import kotlinx.coroutines.launch
import org.leviathan941.retrodromcompanion.R
import org.leviathan941.retrodromcompanion.common.di.DiKeys
import org.leviathan941.retrodromcompanion.firebase.push.Messaging
import org.leviathan941.retrodromcompanion.preferences.PreferencesRepository
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
class PushNotificationModel @Inject constructor(
    private val application: Application,
    @Named(DiKeys.APPLICATION_COROUTINE_SCOPE)
    private val scope: CoroutineScope,
    private val preferencesRepository: PreferencesRepository,
) {
    private val _subscribedPushTopics = MutableStateFlow(emptySet<Messaging.Topic>())
    val subscribedPushTopics: StateFlow<Set<Messaging.Topic>> = _subscribedPushTopics.asStateFlow()

    init {
        scope.launch {
            preferencesRepository.ui
                .cancellable()
                .collect { uiPreferences ->
                    _subscribedPushTopics.value = uiPreferences.subscribedPushTopics
                        .mapNotNull { topicName ->
                            Messaging.topicFromValue(topicName)
                        }.toSet()
                }
        }

        scope.launch {
            preferencesRepository.promoEditor.decrementPushPostsPromoStartsUntilShow()
        }
    }

    fun subscribeToTopic(
        topic: Messaging.Topic,
        showToastOnFailed: Boolean = true,
    ) {
        scope.launch {
            if (Messaging.subscribeToTopic(topic)) {
                preferencesRepository.uiEditor.addPushTopicSubscription(topic.value)
            } else if (showToastOnFailed) {
                showTopicSubscriptionFailedToast(
                    textRes = R.string.push_topic_subscription_failed_toast_text,
                    topicName = Messaging.Topic.NEW_RETRODROM_POSTS.value
                )
            }
        }
    }

    fun unsubscribeFromTopic(
        topic: Messaging.Topic,
        showToastOnFailed: Boolean = true,
    ) {
        scope.launch {
            if (Messaging.unsubscribeFromTopic(Messaging.Topic.NEW_RETRODROM_POSTS)) {
                preferencesRepository.uiEditor.removePushTopicSubscription(topic.value)
            } else if (showToastOnFailed) {
                showTopicSubscriptionFailedToast(
                    textRes = R.string.push_topic_unsubscription_failed_toast_text,
                    topicName = Messaging.Topic.NEW_RETRODROM_POSTS.value
                )
            }
        }
    }

    private fun showTopicSubscriptionFailedToast(
        @StringRes textRes: Int,
        topicName: String,
    ) {
        Toast.makeText(
            application,
            application.getString(
                textRes,
                topicName
            ),
            Toast.LENGTH_SHORT
        ).show()
    }
}