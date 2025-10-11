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

package org.leviathan941.retrodromcompanion.ui.drawer

import android.util.Log
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import org.leviathan941.retrodromcompanion.R

private const val TAG = "SocialNetworkDrawerNav"

@Composable
fun SocialNetworkDrawerNavView(
    title: String,
    modifier: Modifier = Modifier,
    iconUrl: String? = null,
    badgeContentDescription: String? = null,
    isSelected: () -> Boolean = { false },
    onClick: () -> Unit = {},
) {
    val placeholder = rememberVectorPainter(image = Icons.Default.Person)
    DrawerMenuItemView(
        title = title,
        modifier = modifier,
        icon = {
            AsyncImage(
                modifier = Modifier
                    .size(24.dp),
                model = ImageRequest.Builder(LocalContext.current)
                    .data(iconUrl)
                    .build(),
                placeholder = placeholder,
                error = placeholder,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                filterQuality = FilterQuality.Low,
                onError = { error ->
                    Log.e(TAG, "Error while loading image", error.result.throwable)
                },
            )
        },
        badge = {
            Icon(
                painter = painterResource(R.drawable.google_material_open_in_new),
                contentDescription = badgeContentDescription,
            )
        },
        isSelected = isSelected(),
        onClick = onClick,
    )
}
