/*
 * Copyright (c) 2026 Andrew Z.
 * docmanager — a scalable document management system.
 */

package ru.example.docmanager.ui.components

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import docmanager.shared.generated.resources.Res
import docmanager.shared.generated.resources.input_supporting_required
import org.jetbrains.compose.resources.stringResource

@Composable
fun TextInput(
    value: String,
    label: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    singleLine: Boolean = true,
    required: Boolean = false,
    enabled: Boolean = true,
    visualTransformation: VisualTransformation = VisualTransformation.None
) {
    OutlinedTextField(
        value = value,
        enabled = enabled,
        onValueChange = onValueChange,
        label = { Text(label) },
        supportingText = {
            if (required)
                Text(stringResource(Res.string.input_supporting_required))
        },
        singleLine = singleLine,
        shape = RoundedCornerShape(16.dp),
        visualTransformation = visualTransformation,
        modifier = modifier
    )
}