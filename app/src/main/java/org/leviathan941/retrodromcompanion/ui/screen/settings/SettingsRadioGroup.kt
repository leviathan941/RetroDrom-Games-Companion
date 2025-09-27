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

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun <T> SettingsRadioGroup(
    selectedItem: SettingsRadioGroupItem<T>,
    items: List<SettingsRadioGroupItem<T>>,
    onItemChange: (SettingsRadioGroupItem<T>) -> Unit,
    modifier: Modifier = Modifier,
) {
    items.forEach { item ->
        Row(
            modifier = modifier
                .fillMaxWidth()
                .clickable { onItemChange(item) },
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start,
        ) {
            RadioButton(
                modifier = Modifier.padding(vertical = 8.dp),
                selected = item == selectedItem,
                onClick = { onItemChange(item) },
            )
            Text(
                modifier = Modifier.padding(
                    horizontal = 16.dp,
                    vertical = 8.dp,
                ),
                text = item.title,
                style = MaterialTheme.typography.titleLarge,
            )
        }
        HorizontalDivider()
    }
}

@Preview(showBackground = true)
@Composable
@SuppressWarnings("MagicNumber")
private fun SettingsRadioGroupPreview() {
    val items = listOf(
        SettingsRadioGroupItem(1, "Item 1"),
        SettingsRadioGroupItem(2, "Item 2"),
        SettingsRadioGroupItem(3, "Item 3"),
    )
    Column(
        verticalArrangement = Arrangement.SpaceEvenly,
    ) {
        SettingsRadioGroup(
            selectedItem = items.first(),
            items = items,
            onItemChange = {},
        )
    }
}
