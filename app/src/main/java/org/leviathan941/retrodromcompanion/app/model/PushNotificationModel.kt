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

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.cancellable
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.StringResource
import org.leviathan941.retrodromcompanion.resources.Res
import org.leviathan941.retrodromcompanion.resources.push_topic_subscription_failed_message
import org.leviathan941.retrodromcompanion.resources.push_topic_unsubscription_failed_message
import org.leviathan941.retrodromcompanion.app.message.UserMessage
import org.leviathan941.retrodromcompanion.app.message.UserMessages
import org.leviathan941.retrodromcompanion.common.di.ApplicationCoroutineScope
import org.leviathan941.retrodromcompanion.notification.PushMessaging
import org.leviathan941.retrodromcompanion.preferences.PreferencesRepository

@Inject
@SingleIn(AppScope::class)
class PushNotificationModel(
    private val userMessages: UserMessages,
    @param:ApplicationCoroutineScope
    private val scope: CoroutineScope,
    private val preferencesRepository: PreferencesRepository,
    private val pushMessaging: PushMessaging,
) {
    private val _subscribedPushTopics = MutableStateFlow(emptySet<PushMessaging.Topic>())
    val subscribedPushTopics: StateFlow<Set<PushMessaging.Topic>> =
        _subscribedPushTopics.asStateFlow()

    init {
        scope.launch {
            preferencesRepository.ui
                .cancellable()
                .collect { uiPreferences ->
                    _subscribedPushTopics.value = uiPreferences.subscribedPushTopics
                        .mapNotNull { topicName ->
                            PushMessaging.Topic.fromValue(topicName)
                        }.toSet()
                }
        }

        scope.launch {
            preferencesRepository.promoEditor.decrementPushPostsPromoStartsUntilShow()
        }
    }

    fun subscribeToTopic(
        topic: PushMessaging.Topic,
        showMessageOnFailed: Boolean = true,
    ) {
        scope.launch {
            if (pushMessaging.subscribeToTopic(topic)) {
                preferencesRepository.uiEditor.addPushTopicSubscription(topic.value)
            } else if (showMessageOnFailed) {
                showTopicSubscriptionFailedMessage(
                    textRes = Res.string.push_topic_subscription_failed_message,
                    topicName = topic.value,
                )
            }
        }
    }

    fun unsubscribeFromTopic(
        topic: PushMessaging.Topic,
        showMessageOnFailed: Boolean = true,
    ) {
        scope.launch {
            if (pushMessaging.unsubscribeFromTopic(topic)) {
                preferencesRepository.uiEditor.removePushTopicSubscription(topic.value)
            } else if (showMessageOnFailed) {
                showTopicSubscriptionFailedMessage(
                    textRes = Res.string.push_topic_unsubscription_failed_message,
                    topicName = topic.value,
                )
            }
        }
    }

    private fun showTopicSubscriptionFailedMessage(
        textRes: StringResource,
        topicName: String,
    ) {
        userMessages.show(
            UserMessage.Resource(
                textRes = textRes,
                formatArgs = listOf(topicName),
            ),
        )
    }
}
