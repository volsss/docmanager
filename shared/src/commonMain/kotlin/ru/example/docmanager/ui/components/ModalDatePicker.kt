/*
 * Copyright (c) 2026 Andrew Z.
 * docmanager — a scalable document management system.
 */

package ru.example.docmanager.ui.components

import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import docmanager.shared.generated.resources.Res
import docmanager.shared.generated.resources.component_cancel_button
import docmanager.shared.generated.resources.component_ok_button
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
                    Text(stringResource(Res.string.component_ok_button))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = onDismissRequest
                ) {
                    Text(stringResource(Res.string.component_cancel_button))
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}