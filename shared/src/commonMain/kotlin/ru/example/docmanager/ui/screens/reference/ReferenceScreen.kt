/*
 * Copyright (c) 2026 Andrew Z.
 * docmanager — a scalable document management system.
 */

package ru.example.docmanager.ui.screens.reference

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Button
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfoV2
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import docmanager.shared.generated.resources.Res
import docmanager.shared.generated.resources.reference_new_item_button
import docmanager.shared.generated.resources.reference_save_button
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import ru.example.docmanager.data.document.header.HeaderFieldType
import ru.example.docmanager.data.reference.Reference
import ru.example.docmanager.data.reference.ReferenceType
import ru.example.docmanager.ui.Utils
import ru.example.docmanager.ui.components.ReferenceInput
import ru.example.docmanager.ui.router.Route
import ru.example.docmanager.ui.viewmodel.reference.ReferenceUiState
import ru.example.docmanager.ui.viewmodel.reference.ReferenceViewModel

@Composable
fun ReferenceScreen(
    destination: Route.Reference,
    onStatusMessage: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ReferenceViewModel = koinInject()
) {
    val referenceType = destination.type
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(referenceType) {
        viewModel.setType(referenceType)
        if (uiState.itemsByType.isEmpty()) {
            viewModel.loadReferenceItems()
        }
    }

    val currentOnStatusMessage by rememberUpdatedState(onStatusMessage)
    LaunchedEffect(uiState.statusMessage) {
        uiState.statusMessage?.let { message ->
            currentOnStatusMessage(message)
            viewModel.clearStatusMessage()
        }
    }

    ReferenceScreenContent(
        referenceType = referenceType,
        uiState = uiState,
        onSave = viewModel::saveAll,
        onNew = viewModel::createItem,
        onDelete = viewModel::deleteItem,
        onFieldChange = viewModel::updateField,
        modifier = modifier
    )
}

@Composable
fun ReferenceScreenContent(
    referenceType: ReferenceType,
    uiState: ReferenceUiState,
    onSave: () -> Unit,
    onNew: () -> Unit,
    onDelete: (Reference) -> Unit,
    onFieldChange: (itemId: Int, headerFieldType: HeaderFieldType, newValue: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val references = uiState.itemsByType[referenceType].orEmpty()

    Column(
        modifier = modifier.fillMaxSize()
    ) {
        if (uiState.isLoading) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        }

        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ReferenceActions(
                onSave = onSave,
                onNew = onNew,
                enabled = !uiState.isLoading
            )

            if (references.isEmpty() && !uiState.isLoading) {
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
fun ReferenceItems(
    referenceType: ReferenceType,
    uiState: ReferenceUiState,
    references: List<Reference>,
    onFieldChange: (itemId: Int, headerFieldType: HeaderFieldType, newValue: String) -> Unit,
    onDelete: (Reference) -> Unit,
    modifier: Modifier = Modifier
) {
    val windowSizeClass = currentWindowAdaptiveInfoV2().windowSizeClass
    val isWide = windowSizeClass.isWidthAtLeastBreakpoint(Utils.WIDE_BREAKPOINT)

    LazyColumn(
        modifier = modifier.fillMaxSize().then(
            if (isWide) Modifier
            else Modifier.horizontalScroll(rememberScrollState())
        ),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(
            items = references,
            key = { item -> item.id }
        ) { item ->
            ReferenceInput(
                item = item,
                currentValues = uiState.getFieldValues(referenceType, item),
                onFieldChange = onFieldChange,
                onRemove = { onDelete(item) }
            )
        }
    }
}

@Composable
fun ReferenceActions(
    onSave: () -> Unit,
    onNew: () -> Unit,
    enabled: Boolean = true,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Button(
            onClick = onSave,
            enabled = enabled
        ) {
            Text(stringResource(Res.string.reference_save_button))
        }

        Button(
            onClick = onNew,
            enabled = enabled
        ) {
            Text(stringResource(Res.string.reference_new_item_button))
        }
    }
}