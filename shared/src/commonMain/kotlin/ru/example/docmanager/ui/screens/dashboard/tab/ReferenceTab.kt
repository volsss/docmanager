/*
 * Copyright (c) 2026 Andrew Z.
 * docmanager — a scalable document management system.
 */

package ru.example.docmanager.ui.screens.dashboard.tab

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.*
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import docmanager.shared.generated.resources.Res
import docmanager.shared.generated.resources.reference_new_item_button
import docmanager.shared.generated.resources.reference_save_button
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import ru.example.docmanager.reference.Reference
import ru.example.docmanager.reference.ReferenceType
import ru.example.docmanager.ui.Utils
import ru.example.docmanager.ui.components.ReferenceInput
import ru.example.docmanager.viewmodel.DashboardDestination
import ru.example.docmanager.viewmodel.ReferenceState
import ru.example.docmanager.viewmodel.ReferenceViewModel

@Composable
fun ReferenceTab(
    destination: DashboardDestination.Reference,
    viewModel: ReferenceViewModel = koinInject()
) {
    val scope = rememberCoroutineScope()
    val referenceState = viewModel.formState
    val referenceType = destination.type
    val referenceItems = referenceState.currentItems

    LaunchedEffect(destination) {
        viewModel.setType(referenceType)
    }
    LaunchedEffect(Unit) {
        if (referenceState.itemsByType.isEmpty()) {
            viewModel.loadReferenceItems()
        }
    }

    Column (
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        ReferenceBanner(
            referenceState = referenceState,
            referenceType = referenceType
        )

        ReferenceItems(
            referenceState = referenceState,
            references = referenceItems,
            scope = scope,
            referenceViewModel = viewModel
        )

        ReferenceActions(
            scope = scope,
            referenceViewModel = viewModel
        )
    }
}

@Composable
fun ReferenceBanner(
    referenceState: ReferenceState,
    referenceType: ReferenceType
) {
    Text(
        stringResource(referenceType.stringResource),
        style = MaterialTheme.typography.titleLarge
    )
    referenceState.statusMessage?.let { message ->
        Spacer(Modifier.height(8.dp))
        Text(
            text = message,
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
fun ReferenceItems(
    referenceState: ReferenceState,
    references: List<Reference>,
    scope: CoroutineScope,
    referenceViewModel: ReferenceViewModel
) {
    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
    val isWide = windowSizeClass.isWidthAtLeastBreakpoint(Utils.WIDE_BREAKPOINT)
    LazyColumn(
        modifier = Modifier.fillMaxSize().then(
            if (isWide) Modifier
            else Modifier.horizontalScroll(rememberScrollState())
        )
    ) {
        items(references.size) { index ->
            val item = references[index]
            ReferenceInput(
                item = item,
                currentValues = referenceState.fieldValues[item.id].orEmpty(),
                onFieldChange = referenceState::updateField,
                onRemove = {
                    scope.launch {
                        referenceViewModel.deleteItem(item)
                    }
                }
            )
        }
    }
}

@Composable
fun ReferenceActions(
    scope: CoroutineScope,
    referenceViewModel: ReferenceViewModel
) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Button(
            onClick = {
                scope.launch {
                    referenceViewModel.saveAll()
                }
            }
        ) {
            Text(stringResource(Res.string.reference_save_button))
        }

        Button(
            onClick = {
                scope.launch {
                    referenceViewModel.createItem()
                }
            }
        ) {
            Text(stringResource(Res.string.reference_new_item_button))
        }
    }
}