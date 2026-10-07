/*
 * Copyright (c) 2026 Andrew Z.
 * docmanager — a scalable document management system.
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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.window.core.layout.WindowSizeClass
import org.jetbrains.compose.resources.stringResource
import ru.example.docmanager.reference.Reference
import ru.example.docmanager.document.header.HeaderFieldType
import ru.example.docmanager.ui.Utils

@Composable
fun ReferenceInput(
    item: Reference,
    currentValues: Map<HeaderFieldType, String>,
    onFieldChange: (itemId: Int, headerFieldType: HeaderFieldType, newValue: String) -> Unit,
    onRemove: () -> Unit
) {
    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
    val isWide = windowSizeClass.isWidthAtLeastBreakpoint(Utils.WIDE_BREAKPOINT)

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(item.id.toString())

        item.toMap().forEach { (type, value) ->
            OutlinedTextField(
                label = { Text(stringResource(type.stringResource)) },
                modifier = if (isWide) Modifier.weight(1f) else Modifier.widthIn(min = 300.dp),
                value = currentValues[type] ?: value,
                onValueChange = { newValue ->
                    onFieldChange(
                        item.id,
                        type,
                        newValue
                    )
                }
            )
        }

        IconButton(onClick = onRemove) {
            Icon(Icons.Filled.Remove, "Удалить")
        }
    }
}
