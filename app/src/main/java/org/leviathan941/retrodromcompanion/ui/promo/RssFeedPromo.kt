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

package org.leviathan941.retrodromcompanion.ui.promo

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavBackStackEntry
import org.leviathan941.retrodromcompanion.ui.model.RssFeedPromoModel
import org.leviathan941.retrodromcompanion.ui.model.ViewModelKeys
import org.leviathan941.retrodromcompanion.ui.permission.NotificationPermissionView

@Composable
fun RssFeedPromo(
    backStackEntry: NavBackStackEntry,
) {
    val viewModel = hiltViewModel<RssFeedPromoModel>(
        viewModelStoreOwner = backStackEntry,
        key = ViewModelKeys.MAIN_VIEW_PROMO_MODEL,
    )
    val promoState by viewModel.promoState.collectAsState()
    val permissionGranted = remember { mutableStateOf(false) }

    if (promoState.shouldApplyPushPostsPromo) {
        NotificationPermissionView(
            grantedState = permissionGranted,
            allowRationale = false,
            onRationaleDismiss = {},
            onRationaleConfirm = {},
        )

        if (permissionGranted.value) {
            viewModel.subscribePushPostsTopic()
        }
    }
}