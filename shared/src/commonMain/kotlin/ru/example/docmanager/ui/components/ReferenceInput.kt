/*
 * Copyright (c) 2026 Andrew Z.
 * docmanager — a scalable document management system.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License v3.0.
 *
 * Distributed WITHOUT ANY WARRANTY. See the GNU General Public License
 * for more details: <https://gnu.org>
 */

package ru.example.docmanager.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringResource
import ru.example.docmanager.data.document.header.HeaderFieldType
import ru.example.docmanager.data.reference.Reference
import ru.example.docmanager.ui.utils.applyAdaptive

@Composable
fun ReferenceInput(
    item: Reference,
    currentValues: Map<HeaderFieldType, String>,
    onFieldChange: (itemId: Int, headerFieldType: HeaderFieldType, newValue: String) -> Unit,
    onRemove: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(item.id.toString())

        item.toMap().forEach { (type, value) ->
            TextInput(
                value = currentValues[type] ?: value,
                onValueChange = { newValue ->
                    onFieldChange(
                        item.id,
                        type,
                        newValue
                    )
                },
                label = stringResource(type.stringResource),
                modifier = Modifier.applyAdaptive(
                    whenWide = Modifier.weight(1f),
                    whenNotWide = Modifier.widthIn(min = 300.dp)
                ),
            )
        }

        IconButton(onClick = onRemove) {
            Icon(Icons.Filled.Remove, "Удалить")
        }
    }
}
