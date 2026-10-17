/*
 * Copyright (c) 2026 Andrew Z.
 * docmanager — a scalable document management system.
 */

package ru.example.docmanager.ui.components

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.DateRange
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import docmanager.shared.generated.resources.Res
import docmanager.shared.generated.resources.input_supporting_required
import org.jetbrains.compose.resources.stringResource
import ru.example.docmanager.ui.toEpochMillisUtc
import ru.example.docmanager.ui.toLocalDateFromUtc
import ru.example.docmanager.ui.toLocalDateOrNull
import ru.example.docmanager.ui.toRussianString


@Composable
fun DateInput(
    value: String,
    label: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    required: Boolean = false,
    enabled: Boolean = true,
    visualTransformation: VisualTransformation = VisualTransformation.None
) {
    val showDialog = rememberSaveable { mutableStateOf(false) }

    ModalDatePicker(
        showDialog = showDialog.value,
        onDismissRequest = { showDialog.value = false },
        onValueChange = {
            if (it != null)
                onValueChange(it.toLocalDateFromUtc().toRussianString())
        },
        initialValue = value.toLocalDateOrNull()?.toEpochMillisUtc()
    )

    OutlinedTextField(
        value = value,
        enabled = enabled,
        onValueChange = onValueChange,
        label = { Text(label) },
        supportingText = {
            if (required)
                Text(stringResource(Res.string.input_supporting_required))
        },
        singleLine = true,
        shape = RoundedCornerShape(16.dp),
        trailingIcon = {
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
        },
        visualTransformation = visualTransformation,
        modifier = modifier
    )
}