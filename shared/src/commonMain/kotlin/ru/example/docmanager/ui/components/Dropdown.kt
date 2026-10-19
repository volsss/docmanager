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

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import docmanager.shared.generated.resources.Res
import docmanager.shared.generated.resources.component_dropdown_placeholder
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
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
            DropdownMode.VALUES_ONLY -> {
                items
            }

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
            containerColor = Color.Transparent,
            tonalElevation = 0.dp,
            shadowElevation = 0.dp,
            modifier = Modifier
                .exposedDropdownSize()
                .animateContentSize(
                    animationSpec = tween(durationMillis = 200)
                ),
        ) {
            AnimatedVisibility(
                visible = expanded,
                enter = fadeIn(
                    animationSpec = tween(180)
                ) + expandVertically(
                    animationSpec = tween(220)
                ),
                exit = fadeOut(
                    animationSpec = tween(120)
                ) + shrinkVertically(
                    animationSpec = tween(180)
                ),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .animateContentSize(),
                    verticalArrangement = Arrangement.spacedBy(ListItemDefaults.SegmentedGap)
                ) {
                    filteredItems.forEachIndexed { index, item ->
                        val selected = item == value

                        SegmentedListItem(
                            selected = selected,
                            shapes = ListItemDefaults.segmentedShapes(
                                index = index,
                                count = filteredItems.size
                            ),
                            colors = ListItemDefaults.segmentedColors(
                                containerColor = MaterialTheme.colorScheme.surfaceContainer,
                                contentColor = MaterialTheme.colorScheme.onSurface,
                            ),
                            onClick = { selectItem(item) },
                            trailingContent = {
                                AnimatedVisibility(
                                    visible = selected,
                                    enter = fadeIn(tween(160)) +
                                            expandVertically(tween(160)),
                                    exit = fadeOut(tween(100)) +
                                            shrinkVertically(tween(100)),
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.Check,
                                        contentDescription = "Selected",
                                        modifier = Modifier
                                            .padding(start = 12.dp)
                                            .size(20.dp),
                                    )
                                }
                            }
                        ) {
                            Text(text = item)
                        }
                    }

                    if (filteredItems.isEmpty()) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.surfaceContainer)
                                .padding(16.dp)
                        ) {
                            Text(
                                text = "Элементов нет",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }
        }
    }
}