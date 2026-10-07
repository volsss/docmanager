/*
 * Copyright (c) 2026 Andrew Z.
 * docmanager — a scalable document management system.
 */

package ru.example.docmanager.ui.screens.dashboard.tab

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.*
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import docmanager.shared.generated.resources.Res
import docmanager.shared.generated.resources.reference_new_item_button
import docmanager.shared.generated.resources.reference_save_button
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import ru.example.docmanager.ui.Utils
import ru.example.docmanager.ui.components.ReferenceInput
import ru.example.docmanager.viewmodel.DashboardDestination
import ru.example.docmanager.viewmodel.ReferenceViewModel

@Composable
fun ReferenceTab(
    destination: DashboardDestination.Reference,
    referenceViewModel: ReferenceViewModel = koinInject()
) {
    val scope = rememberCoroutineScope()
    val formState = referenceViewModel.formState
    val referenceType = destination.type
    val referenceItems = formState.currentItems
    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
    val isWide = windowSizeClass.isWidthAtLeastBreakpoint(Utils.WIDE_BREAKPOINT)

    LaunchedEffect(destination) {
        referenceViewModel.setType(referenceType)
    }
    LaunchedEffect(Unit) {
        if (formState.itemsByType.isEmpty()) {
            referenceViewModel.loadReferenceItems()
        }
    }

    Text(
        stringResource(referenceType.stringResource),
        style = MaterialTheme.typography.titleLarge
    )
    formState.statusMessage?.let { message ->
        Spacer(Modifier.height(8.dp))
        Text(
            text = message,
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.bodyMedium
        )
    }

    Spacer(Modifier.height(16.dp))

    Column(
        Modifier.fillMaxSize().then(
            if (isWide) Modifier
            else Modifier.horizontalScroll(rememberScrollState())
        )
    ) {
        referenceItems.forEach { item ->
            ReferenceInput(
                item = item,
                currentValues = formState.fieldValues[item.id].orEmpty(),
                onFieldChange = formState::updateField,
                onRemove = {
                    scope.launch {
                        referenceViewModel.deleteItem(item)
                    }
                },
                windowSizeClass = windowSizeClass
            )
        }
    }

    Spacer(Modifier.height(16.dp))

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