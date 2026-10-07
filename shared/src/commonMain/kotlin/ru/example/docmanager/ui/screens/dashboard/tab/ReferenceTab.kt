/*
 * Copyright (c) 2026 Andrew Z.
 * docmanager — a scalable document management system.
 */

package ru.example.docmanager.ui.screens.dashboard.tab

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
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
    snackbarHostState: SnackbarHostState,
    viewModel: ReferenceViewModel = koinInject()
) {
    val scope = rememberCoroutineScope()
    val referenceState = viewModel.formState
    val referenceType = destination.type
    val referenceItems = referenceState.itemsByType[referenceType].orEmpty()

    LaunchedEffect(destination) {
        viewModel.setType(referenceType)
    }
    LaunchedEffect(Unit) {
        if (referenceState.itemsByType.isEmpty()) {
            viewModel.loadReferenceItems()
        }
    }
    LaunchedEffect(referenceState.statusMessage) {
        referenceState.statusMessage?.let {
            snackbarHostState.showSnackbar(it)
        }
    }

    Column (
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        ReferenceBanner(
            referenceType = referenceType,
            scope = scope,
            viewModel = viewModel
        )

        ReferenceItems(
            referenceState = referenceState,
            references = referenceItems,
            scope = scope,
            viewModel = viewModel
        )
    }
}

@Composable
fun ReferenceBanner(
    referenceType: ReferenceType,
    scope: CoroutineScope,
    viewModel: ReferenceViewModel,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            stringResource(referenceType.stringResource),
            style = MaterialTheme.typography.titleLarge
        )

        ReferenceActions(
            scope = scope,
            viewModel = viewModel
        )
    }
}

@Composable
fun ReferenceItems(
    referenceState: ReferenceState,
    references: List<Reference>,
    scope: CoroutineScope,
    viewModel: ReferenceViewModel,
    modifier: Modifier = Modifier
) {
    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
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
                onRemove = {
                    scope.launch {
                        viewModel.deleteItem(item)
                    }
                }
            )
        }
    }
}

@Composable
fun ReferenceActions(
    scope: CoroutineScope,
    viewModel: ReferenceViewModel,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Button(
            onClick = {
                scope.launch {
                    viewModel.saveAll()
                }
            }
        ) {
            Text(stringResource(Res.string.reference_save_button))
        }

        Button(
            onClick = {
                scope.launch {
                    viewModel.createItem()
                }
            }
        ) {
            Text(stringResource(Res.string.reference_new_item_button))
        }
    }
}