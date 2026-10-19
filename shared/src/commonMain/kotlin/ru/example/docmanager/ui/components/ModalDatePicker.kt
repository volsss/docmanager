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

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import docmanager.shared.generated.resources.Res
import docmanager.shared.generated.resources.component_datepicker_dismiss_button
import docmanager.shared.generated.resources.component_datepicker_confirm_button
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModalDatePicker(
    onValueChange: (Long?) -> Unit,
    showDialog: Boolean = false,
    initialValue: Long? = null,
    onDismissRequest: () -> Unit = { },
) {
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = initialValue
    )

    if (showDialog) {
        DatePickerDialog(
            onDismissRequest = onDismissRequest,
            confirmButton = {
                TextButton(
                    onClick = {
                        onValueChange(datePickerState.selectedDateMillis)
                        onDismissRequest()
                    }
                ) {
                    Text(stringResource(Res.string.component_datepicker_confirm_button))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = onDismissRequest
                ) {
                    Text(stringResource(Res.string.component_datepicker_dismiss_button))
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}