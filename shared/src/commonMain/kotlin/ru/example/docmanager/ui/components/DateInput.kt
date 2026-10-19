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

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.DateRange
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import docmanager.shared.generated.resources.Res
import docmanager.shared.generated.resources.component_input_required
import kotlinx.datetime.LocalDate
import org.jetbrains.compose.resources.stringResource
import ru.example.docmanager.ui.utils.parseRussianOrNull
import ru.example.docmanager.ui.utils.toEpochMillisUtc
import ru.example.docmanager.ui.utils.toLocalDateFromUtc
import ru.example.docmanager.ui.utils.toRussianString


@Composable
fun DateInput(
    value: LocalDate,
    label: String,
    onValueChange: (LocalDate) -> Unit,
    modifier: Modifier = Modifier,
    required: Boolean = false,
    enabled: Boolean = true,
    visualTransformation: VisualTransformation = VisualTransformation.None
) {
    var textValue by remember(value) { mutableStateOf(value.toRussianString()) }
    val currentInitialValue = remember(value) { value.toEpochMillisUtc() }
    val showDialog = rememberSaveable { mutableStateOf(false) }

    if (enabled) {
        ModalDatePicker(
            showDialog = showDialog.value,
            onDismissRequest = { showDialog.value = false },
            onValueChange = { selectedDate ->
                if (selectedDate != null)
                    onValueChange(selectedDate.toLocalDateFromUtc())
            },
            initialValue = currentInitialValue
        )
    }

    OutlinedTextField(
        value = textValue,
        enabled = enabled,
        onValueChange = { rawInput ->
            textValue = rawInput
            rawInput.parseRussianOrNull()
                ?: runCatching {
                    LocalDate.parse(rawInput)
                }.getOrNull()?.let { parsedDate ->
                    onValueChange(parsedDate)
                }
        },
        label = { Text(label) },
        supportingText = {
            if (required)
                Text(stringResource(Res.string.component_input_required))
        },
        singleLine = true,
        shape = RoundedCornerShape(16.dp),
        trailingIcon = {
            if (enabled) {
                IconButton(
                    onClick = {
                        showDialog.value = true
                    }
                ) {
                    Icon(
                        Icons.Rounded.DateRange,
                        contentDescription = null
                    )
                }
            }
        },
        visualTransformation = visualTransformation,
        modifier = modifier
    )
}