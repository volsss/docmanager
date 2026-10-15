/*
 * Copyright (c) 2026 Andrew Z.
 * docmanager — a scalable document management system.
 */

package ru.example.docmanager.ui.screens

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfoV2
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import docmanager.shared.generated.resources.Res
import docmanager.shared.generated.resources.reference_new_item_button
import docmanager.shared.generated.resources.reference_save_button
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import ru.example.docmanager.reference.Reference
import ru.example.docmanager.ui.Utils
import ru.example.docmanager.ui.components.ReferenceInput
import ru.example.docmanager.ui.components.ScreenTopBar
import ru.example.docmanager.ui.navigation.Screen
import ru.example.docmanager.viewmodel.ReferenceState
import ru.example.docmanager.viewmodel.ReferenceViewModel

@Composable
fun ReferenceScreen(
    destination: Screen.Reference,
    onBack: () -> Unit,
    onStatusMessage: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ReferenceViewModel = koinInject()
) {
    val referenceType = destination.type
    val referenceState = viewModel.formState

    val referenceItems = remember(referenceState.itemsByType, referenceType) {
        referenceState.itemsByType[referenceType].orEmpty()
    }

    LaunchedEffect(referenceType) {
        viewModel.setType(referenceType)
        if (referenceState.itemsByType.isEmpty()) {
            viewModel.loadReferenceItems()
        }
    }
    val currentOnStatusMessage by rememberUpdatedState(onStatusMessage)
    LaunchedEffect(referenceState.statusMessage) {
        referenceState.statusMessage?.let { message ->
            currentOnStatusMessage(message)
            viewModel.clearStatusMessage()
        }
    }

    Column(
        modifier = modifier.fillMaxSize()
    ) {
        ScreenTopBar(
            title = stringResource(destination.type.stringResource),
            onBack = onBack
        )

        Column (
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ReferenceActions(
                onSave = viewModel::saveAll,
                onNew = viewModel::createItem
            )

            ReferenceItems(
                referenceState = referenceState,
                references = referenceItems,
                onDelete = viewModel::deleteItem
            )
        }
    }
}

@Composable
fun ReferenceItems(
    referenceState: ReferenceState,
    references: List<Reference>,
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
            count = references.size,
            key = { index -> references[index].id }
        ) { index ->
            val item = references[index]
            ReferenceInput(
                item = item,
                currentValues = referenceState.fieldValues[item.id].orEmpty(),
                onFieldChange = referenceState::updateField,
                onRemove = { onDelete(item) }
            )
        }
    }
}

@Composable
fun ReferenceActions(
    onSave: () -> Unit,
    onNew: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Button(onClick = onSave) {
            Text(stringResource(Res.string.reference_save_button))
        }

        Button(onClick = onNew) {
            Text(stringResource(Res.string.reference_new_item_button))
        }
    }
}