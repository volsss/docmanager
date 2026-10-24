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
import ru.example.docmanager.data.reference.Reference

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun <T : Reference> ReferenceDropdown(
    value: T?,
    items: List<T>,
    itemName: (T?) -> String,
    onValueChange: (T?) -> Unit,
    modifier: Modifier = Modifier,
    itemSupportingText: ((T?) -> String)? = null,
    label: String? = null,
    placeholder: String? = null,
    enabled: Boolean = true,
    keyboardOptions: KeyboardOptions = KeyboardOptions(
        imeAction = ImeAction.Done
    ),
) {
    var expanded by remember { mutableStateOf(false) }

    var draft by remember(value) {
        mutableStateOf(itemName(value))
    }

    LaunchedEffect(value) {
        if (!expanded) {
            draft = itemName(value)
        }
    }

    val filteredItems = remember(items, draft) {
        if (draft.isBlank()) {
            items
        } else {
            items.filter {
                itemName(it).contains(
                    other = draft,
                    ignoreCase = true
                )
            }
        }
    }

    fun selectItem(item: T) {
        draft = itemName(item)
        onValueChange(item)
        expanded = false
    }

    fun finishEditing() {
        val exactItem = items.firstOrNull {
            itemName(it) == draft
        }
        if (exactItem != null) {
            onValueChange(exactItem)
            draft = itemName(exactItem)
            expanded = false
        } else {
            draft = itemName(value)
            expanded = false
        }
    }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { shouldExpand ->
            if (!enabled) return@ExposedDropdownMenuBox
            expanded = shouldExpand
            if (shouldExpand) {
                draft = itemName(value)
            }
        },
        modifier = modifier,
    ) {
        OutlinedTextField(
            value = draft,
            onValueChange = { newValue ->
                draft = newValue
                expanded = true
            },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(
                    type = ExposedDropdownMenuAnchorType.PrimaryEditable,
                    enabled = enabled,
                ),
            enabled = enabled,
            label = label?.let { { Text(it) } },
            placeholder = placeholder?.let { { Text(it) } },
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
                onValueChange(items.firstOrNull { itemName(it) == draft })
                draft = itemName(value)
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
                            supportingContent = itemSupportingText?.let {
                                { Text(it.invoke(item)) }
                            },
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
                            Text(itemName(item))
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
