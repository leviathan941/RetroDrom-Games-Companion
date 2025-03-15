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

package org.leviathan941.retrodromcompanion.ui.model

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.cancellable
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import org.leviathan941.retrodromcompanion.app.model.PushNotificationModel
import org.leviathan941.retrodromcompanion.firebase.push.Messaging
import org.leviathan941.retrodromcompanion.preferences.PreferencesRepository
import javax.inject.Inject

private const val TAG = "MainViewPromoModel"

@HiltViewModel
class RssFeedPromoModel @Inject constructor(
    private val preferencesRepository: PreferencesRepository,
    private val pushNotificationModel: PushNotificationModel,
) : ViewModel() {

    val promoState: StateFlow<RssFeedPromoState> = run {
        val isPushPostsPromoAllowed = preferencesRepository.promo
            .cancellable()
            .map { prefs ->
                prefs.pushPosts.run {
                    Log.d(TAG, "Push posts prefs: startsUntilShow: $startsUntilShow")
                    startsUntilShow < 1
                }
            }

        val isPushPostsTopicSubscribed = pushNotificationModel.subscribedPushTopics.map {
            Messaging.Topic.NEW_RETRODROM_POSTS in it
        }

        combine(isPushPostsPromoAllowed, isPushPostsTopicSubscribed) {
            isAllowed, isSubscribed ->
            Log.d(TAG, "Set promo state: isAllowed: $isAllowed, isSubscribed: $isSubscribed")
            RssFeedPromoState(
                shouldApplyPushPostsPromo = isAllowed && !isSubscribed
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = RssFeedPromoState(
                shouldApplyPushPostsPromo = false
            )
        )
    }

    fun subscribePushPostsTopic() {
        pushNotificationModel.subscribeToTopic(
            topic = Messaging.Topic.NEW_RETRODROM_POSTS,
            showToastOnFailed = false,
        )
    }
}