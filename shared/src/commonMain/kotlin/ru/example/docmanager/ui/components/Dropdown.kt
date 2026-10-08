/*
 * Copyright (c) 2026 Andrew Z.
 * docmanager — a scalable document management system.
 */

package ru.example.docmanager.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import docmanager.shared.generated.resources.Res
import docmanager.shared.generated.resources.component_dropdown_placeholder
import org.jetbrains.compose.resources.stringResource
import ru.example.docmanager.viewmodel.DashboardDestination.Documents.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Dropdown(
    value: String,
    items: List<String>,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    mode: DropdownMode = DropdownMode.VALUES_ONLY,
    label: String? = null,
    placeholder: String? = null,
    enabled: Boolean = true,
    isError: Boolean = false,
    supportingText: String? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions(
        imeAction = ImeAction.Done
    ),
) {
    var expanded by remember { mutableStateOf(false) }

    var draft by remember(value) {
        mutableStateOf(value)
    }

    LaunchedEffect(value) {
        if (!expanded) {
            draft = value
        }
    }

    val isEditable = mode != DropdownMode.VALUES_ONLY

    val text = when (mode) {
        DropdownMode.VALUES_ONLY,
        DropdownMode.VALUES_AND_INPUT -> value
        DropdownMode.STRICT_VALUES_AND_INPUT -> draft
    }

    val filteredItems = remember(items, text, mode) {
        when (mode) {
            DropdownMode.VALUES_ONLY -> { items }
            DropdownMode.VALUES_AND_INPUT,
            DropdownMode.STRICT_VALUES_AND_INPUT -> {
                if (text.isBlank()) {
                    items
                } else {
                    items.filter {
                        it.contains(
                            other = text,
                            ignoreCase = true
                        )
                    }
                }
            }
        }
    }

    val strictValueIsValid = remember(items, draft) {
        items.any { it == draft }
    }

    val effectiveIsError = isError
            || (mode == DropdownMode.STRICT_VALUES_AND_INPUT
            && draft.isNotEmpty() && !strictValueIsValid)

    val effectiveSupportingText = when {
        supportingText != null -> supportingText
        mode == DropdownMode.STRICT_VALUES_AND_INPUT &&
                draft.isNotEmpty() &&
                !strictValueIsValid ->
            stringResource(Res.string.component_dropdown_placeholder)
        else -> null
    }

    fun selectItem(item: String) {
        draft = item
        onValueChange(item)
        expanded = false
    }

    fun finishEditing() {
        when (mode) {
            DropdownMode.VALUES_ONLY -> {
                expanded = false
            }
            DropdownMode.VALUES_AND_INPUT -> {
                onValueChange(text)
                expanded = false
            }
            DropdownMode.STRICT_VALUES_AND_INPUT -> {
                val exactItem = items.firstOrNull { it == draft }
                if (exactItem != null) {
                    onValueChange(exactItem)
                    draft = exactItem
                    expanded = false
                } else {
                    draft = value
                    expanded = false
                }
            }
        }
    }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { shouldExpand ->
            if (!enabled) return@ExposedDropdownMenuBox
            expanded = shouldExpand
            if (shouldExpand && mode == DropdownMode.STRICT_VALUES_AND_INPUT) {
                draft = value
            }
        },
        modifier = modifier,
    ) {
        OutlinedTextField(
            value = text,
            onValueChange = { newValue ->
                when (mode) {
                    DropdownMode.VALUES_ONLY -> Unit
                    DropdownMode.VALUES_AND_INPUT -> {
                        onValueChange(newValue)
                        expanded = true
                    }
                    DropdownMode.STRICT_VALUES_AND_INPUT -> {
                        draft = newValue
                        expanded = true
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(
                    type = if (isEditable) {
                        ExposedDropdownMenuAnchorType.PrimaryEditable
                    } else {
                        ExposedDropdownMenuAnchorType.PrimaryNotEditable
                    },
                    enabled = enabled,
                ),
            enabled = enabled,
            readOnly = !isEditable,
            label = label?.let { { Text(it) } },
            placeholder = placeholder?.let { { Text(it) } },
            isError = effectiveIsError,
            supportingText = effectiveSupportingText?.let {
                { Text(it) }
            },
            singleLine = true,
            shape = RoundedCornerShape(16.dp),
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(
                    expanded = expanded
                )
            },
            keyboardOptions = keyboardOptions,
            keyboardActions = KeyboardActions(
                onDone = {
                    finishEditing()
                }
            ),
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = {
                when (mode) {
                    DropdownMode.STRICT_VALUES_AND_INPUT -> {
                        if (items.any { it == draft }) {
                            onValueChange(draft)
                        }
                        draft = value
                    }
                    else -> Unit
                }
                expanded = false
            },
            modifier = Modifier.exposedDropdownSize(),
        ) {
            filteredItems.forEach { item ->
                DropdownMenuItem(
                    text = {
                        Text(item)
                    },
                    onClick = {
                        selectItem(item)
                    },
                    trailingIcon = if (item == value) {
                        {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                            )
                        }
                    } else {
                        null
                    },
                )
            }
        }
    }
}