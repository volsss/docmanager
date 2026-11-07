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

package ru.example.docmanager.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import docmanager.shared.generated.resources.Res
import docmanager.shared.generated.resources.component_back_button
import docmanager.shared.generated.resources.reference_fab
import docmanager.shared.generated.resources.reference_save_button
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import ru.example.docmanager.domain.document.header.HeaderFieldType
import ru.example.docmanager.domain.reference.Reference
import ru.example.docmanager.domain.reference.ReferenceType
import ru.example.docmanager.ui.components.AppSnackbarHost
import ru.example.docmanager.ui.components.LoadingBox
import ru.example.docmanager.ui.components.TextInput
import ru.example.docmanager.ui.router.Route
import ru.example.docmanager.ui.utils.applyAdaptive
import ru.example.docmanager.ui.viewmodel.reference.ReferenceUiState
import ru.example.docmanager.ui.viewmodel.reference.ReferenceViewModel

@Composable
fun ReferenceScreen(
    destination: Route.Reference,
    viewModel: ReferenceViewModel,
    canGoBack: Boolean,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val referenceType = destination.type
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    fun showSnackbar(message: String) {
        scope.launch {
            snackbarHostState.showSnackbar(message)
        }
    }

    LaunchedEffect(referenceType) {
        viewModel.setType(referenceType)
        if (uiState.itemsByType.isEmpty()) {
            viewModel.loadReferenceItems(
                onError = { message ->
                    showSnackbar(message)
                }
            )
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                navigationIcon = {
                    AnimatedVisibility(
                        visible = canGoBack,
                        enter = expandHorizontally(),
                        exit = shrinkHorizontally()
                    ) {
                        IconButton(onClick = onBack) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                                contentDescription = stringResource(Res.string.component_back_button),
                            )
                        }
                    }
                },
                title = {
                    Text(
                        text = stringResource(referenceType.stringResource),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                actions = {
                    FilledTonalIconButton(
                        onClick = {
                            viewModel.saveAll(
                                onError = { message ->
                                    showSnackbar(message)
                                }
                            )
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Save,
                            contentDescription = stringResource(Res.string.reference_save_button)
                        )
                    }
                }
            )
        },
        snackbarHost = { AppSnackbarHost(snackbarHostState) },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                text = { Text(stringResource(Res.string.reference_fab)) },
                icon = {
                    Icon(
                        imageVector = Icons.Rounded.Add,
                        contentDescription = stringResource(Res.string.reference_fab)
                    )
                },
                onClick = {
                    viewModel.createItem(
                        onError = {
                            showSnackbar(it)
                        }
                    )
                }
            )
        }
    ) { innerPadding ->
        ReferenceContent(
            referenceType = referenceType,
            uiState = uiState,
            onDelete = {
                viewModel.deleteItem(it) {
                    showSnackbar(it)
                }
            },
            onFieldChange = viewModel::updateField,
            modifier = Modifier.padding(innerPadding)
        )
    }
}

@Composable
private fun ReferenceContent(
    referenceType: ReferenceType,
    uiState: ReferenceUiState,
    onDelete: (Reference) -> Unit,
    onFieldChange: (itemId: Int, headerFieldType: HeaderFieldType, newValue: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val references = uiState.itemsByType[referenceType].orEmpty()

    if (uiState.isLoading) {
        LoadingBox(modifier = modifier)
        return
    }

    Column(
        modifier = modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (references.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Записи отсутствуют",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                ReferenceItems(
                    referenceType = referenceType,
                    uiState = uiState,
                    references = references,
                    onFieldChange = onFieldChange,
                    onDelete = onDelete
                )
            }
        }
    }
}

@Composable
private fun ReferenceItems(
    referenceType: ReferenceType,
    uiState: ReferenceUiState,
    references: List<Reference>,
    onFieldChange: (itemId: Int, headerFieldType: HeaderFieldType, newValue: String) -> Unit,
    onDelete: (Reference) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize()
            .applyAdaptive(
                whenNotWide = Modifier.horizontalScroll(rememberScrollState())
            ),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(
            items = references,
            key = { item -> item.id }
        ) { item ->
            ReferenceItemRow(
                item = item,
                currentValues = uiState.getFieldValues(referenceType, item),
                onFieldChange = onFieldChange,
                onRemove = { onDelete(item) }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ReferenceItemRow(
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
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(MaterialShapes.Slanted.toShape())
                .background(MaterialTheme.colorScheme.secondary),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = item.id.toString(),
                color = MaterialTheme.colorScheme.onSecondary,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold
            )
        }

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

        FilledTonalIconButton(
            onClick = onRemove
        ) {
            Icon(
                imageVector = Icons.Rounded.Delete,
                contentDescription = "Удалить строку"
            )
        }
    }
}