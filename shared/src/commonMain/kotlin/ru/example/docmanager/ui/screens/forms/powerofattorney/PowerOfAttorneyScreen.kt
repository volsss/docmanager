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

package ru.example.docmanager.ui.screens.forms.powerofattorney

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import docmanager.shared.generated.resources.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import ru.example.docmanager.data.document.Document
import ru.example.docmanager.data.document.header.HeaderFieldType
import ru.example.docmanager.data.reference.Individual
import ru.example.docmanager.data.reference.Organization
import ru.example.docmanager.data.reference.Product
import ru.example.docmanager.data.reference.Supplier
import ru.example.docmanager.ui.components.DateInput
import ru.example.docmanager.ui.components.IntInput
import ru.example.docmanager.ui.components.ReferenceDropdown
import ru.example.docmanager.ui.components.TextInput
import ru.example.docmanager.ui.router.NavigationType
import ru.example.docmanager.ui.router.Route
import ru.example.docmanager.ui.utils.adaptiveWidth
import ru.example.docmanager.ui.utils.applyAdaptive
import ru.example.docmanager.ui.viewmodel.forms.powerofattorney.PowerOfAttorneyBodyItemState
import ru.example.docmanager.ui.viewmodel.forms.powerofattorney.PowerOfAttorneyUiState
import ru.example.docmanager.ui.viewmodel.forms.powerofattorney.PowerOfAttorneyViewModel
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun PowerOfAttorneyScreen(
    document: Document?,
    onStatusMessage: (String) -> Unit,
    navigateTo: (Route, NavigationType) -> Unit,
    registerFabClickAction: ((() -> Unit)?) -> Unit,
    scrollState: ScrollState,
    viewModel: PowerOfAttorneyViewModel = koinInject()
) {
    val scope = rememberCoroutineScope()
    val formState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(document) {
        viewModel.onInit(
            onStatusUpdate = { message ->
                onStatusMessage(message)
            },
            document = document
        )
    }

    if (formState.isLoading) {
        LoadingContent()
        return
    }

    LaunchedEffect(scope, registerFabClickAction, formState) {
        registerFabClickAction {
            viewModel.addBodyItem()
            scope.launch {
                delay(280.milliseconds)
                scrollState.animateScrollTo(
                    value = scrollState.maxValue
                )
            }
        }
    }

    Column(
        modifier = Modifier.adaptiveWidth(),
        verticalArrangement = Arrangement.spacedBy(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        PowerOfAttorneyActions(
            isEditing = formState.isEditing,
            onSaveDocument = {
                viewModel.onSaveDocument(
                    onStatusUpdate = onStatusMessage,
                    onDocumentSaved = { newDocument ->
                        navigateTo(
                            Route.Form(formState.type, newDocument),
                            NavigationType.REPLACE
                        )
                    }
                )
            },
            onSaveAsNewDocument = {
                viewModel.onSaveAsNewDocument(
                    onStatusUpdate = onStatusMessage,
                    onDocumentSaved = { newDocument ->
                        navigateTo(
                            Route.Form(formState.type, newDocument),
                            NavigationType.REPLACE
                        )
                    }
                )
            },
            onDeleteDocument = {
                viewModel.onDeleteDocument(
                    onStatusUpdate = onStatusMessage,
                    onDocumentDeleted = {
                        navigateTo(
                            Route.Documents(formState.type),
                            NavigationType.REPLACE
                        )
                    }
                )
            },
            onExportDocument = {
                viewModel.onExportToFile(
                    onStatusUpdate = onStatusMessage
                )
            },
            onPrintDocument = {
                viewModel.onPrintDocument(
                    onStatusUpdate = onStatusMessage
                )
            }
        )

        PowerOfAttorneyHeaderSection(
            formState = formState,
            onNumberChanged = viewModel::onNumberChanged,
            onDischargeDateChanged = viewModel::onDischargeDateChanged,
            onEndDateChanged = viewModel::onEndDateChanged,
            onOrganizationChanged = viewModel::onOrganizationChanged,
            onIndividualChanged = viewModel::onIndividualChanged,
            onSupplierChanged = viewModel::onSupplierChanged,
            onSupplierAgreementChanged = viewModel::onSupplierAgreementChanged,
        )

        PowerOfAttorneyBodySection(
            formState = formState,
            onUpdateBodyItem = viewModel::updateBodyItem,
            onRemoveBodyItem = viewModel::removeBodyItem,
        )
    }

    Spacer(Modifier.height(64.dp))
}

@Composable
private fun PowerOfAttorneySection(
    label: String,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val isExpanded = remember { mutableStateOf(true) }
    Card(
        modifier = modifier.fillMaxWidth().animateContentSize(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        )
    ) {
        Row(
            modifier = Modifier.fillMaxWidth()
                .clickable { isExpanded.value = !isExpanded.value }
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.weight(1f))

            Icon(
                imageVector = if (isExpanded.value)
                    Icons.Rounded.KeyboardArrowUp
                else Icons.Rounded.KeyboardArrowDown,
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = MaterialTheme.colorScheme.onSurface
            )
        }

        if (isExpanded.value) {
            content()
        }
    }
}

@Composable
private fun PowerOfAttorneyHeaderSection(
    formState: PowerOfAttorneyUiState,
    onNumberChanged: (Int) -> Unit,
    onDischargeDateChanged: (LocalDate) -> Unit,
    onEndDateChanged: (LocalDate) -> Unit,
    onOrganizationChanged: (Organization?) -> Unit,
    onIndividualChanged: (Individual?) -> Unit,
    onSupplierChanged: (Supplier?) -> Unit,
    onSupplierAgreementChanged: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    PowerOfAttorneySection(
        label = stringResource(Res.string.form_section_header),
        modifier = modifier
    ) {
        Column(
            modifier = modifier.fillMaxWidth().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            IntInput(
                value = formState.number,
                onValueChange = onNumberChanged,
                label = stringResource(HeaderFieldType.NUMBER.stringResource),
                modifier = Modifier.fillMaxWidth()
            )

            DateInput(
                value = formState.dischargeDate,
                onValueChange = onDischargeDateChanged,
                label = stringResource(HeaderFieldType.DISCHARGE_DATE.stringResource),
                modifier = Modifier.fillMaxWidth()
            )

            DateInput(
                value = formState.endDate,
                onValueChange = onEndDateChanged,
                label = stringResource(HeaderFieldType.END_DATE.stringResource),
                modifier = Modifier.fillMaxWidth()
            )

            ReferenceDropdown(
                value = formState.organization,
                items = formState.organizations,
                itemName = { it?.name?.ifBlank { "ID: ${it.id}" } ?: "" },
                onValueChange = onOrganizationChanged,
                label = stringResource(Res.string.form_field_organization),
                modifier = Modifier.fillMaxWidth()
            )

            ReferenceDropdown(
                value = formState.individual,
                items = formState.individuals,
                itemName = { it?.name?.ifBlank { "ID: ${it.id}" } ?: "" },
                itemSupportingText = { it?.job ?: "Unknown job" },
                onValueChange = onIndividualChanged,
                label = stringResource(Res.string.form_field_individual),
                modifier = Modifier.fillMaxWidth()
            )

            ReferenceDropdown(
                value = formState.supplier,
                items = formState.suppliers,
                itemName = { it?.name?.ifBlank { "ID: ${it.id}" } ?: "" },
                onValueChange = onSupplierChanged,
                label = stringResource(Res.string.form_field_supplier),
                modifier = Modifier.fillMaxWidth()
            )

            TextInput(
                value = formState.supplierAgreement,
                onValueChange = onSupplierAgreementChanged,
                label = stringResource(HeaderFieldType.SUPPLIER_AGREEMENT.stringResource),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun PowerOfAttorneyBodySection(
    formState: PowerOfAttorneyUiState,
    onUpdateBodyItem: (index: Int, item: PowerOfAttorneyBodyItemState) -> Unit,
    onRemoveBodyItem: (index: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val bodyItems = formState.bodyItems

    PowerOfAttorneySection(
        label = stringResource(Res.string.form_section_body),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
                .applyAdaptive(
                    whenNotWide = Modifier.horizontalScroll(
                        rememberScrollState()
                    )
                )
                .padding(16.dp)
                .animateContentSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            bodyItems.forEachIndexed { index, item ->
                BodyItemRow(
                    index = index,
                    item = item,
                    products = formState.products,
                    onItemChange = { updated ->
                        onUpdateBodyItem(index, updated)
                    },
                    onDelete = { onRemoveBodyItem(index) }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun BodyItemRow(
    index: Int,
    item: PowerOfAttorneyBodyItemState,
    products: List<Product>,
    onItemChange: (PowerOfAttorneyBodyItemState) -> Unit,
    onDelete: () -> Unit
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
                text = "${index + 1}",
                color = MaterialTheme.colorScheme.onSecondary,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold
            )
        }

        ReferenceDropdown(
            value = item.product,
            items = products,
            itemName = { it?.name?.ifBlank { "ID: ${it.id}" } ?: "" },
            onValueChange = { onItemChange(item.copy(product = it)) },
            label = stringResource(Res.string.form_field_product),
            modifier = Modifier.applyAdaptive(
                whenWide = Modifier.weight(2f),
                whenNotWide = Modifier.requiredWidth(300.dp)
            )
        )

        TextInput(
            value = item.unit,
            onValueChange = { onItemChange(item.copy(unit = it)) },
            label = stringResource(Res.string.body_field_unit),
            singleLine = true,
            modifier = Modifier.applyAdaptive(
                whenWide = Modifier.weight(1f),
                whenNotWide = Modifier.widthIn(min = 150.dp)
            )
        )

        TextInput(
            value = item.count,
            onValueChange = { onItemChange(item.copy(count = it)) },
            label = stringResource(Res.string.body_field_count),
            singleLine = true,
            modifier = Modifier.applyAdaptive(
                whenWide = Modifier.weight(1.5f),
                whenNotWide = Modifier.widthIn(min = 300.dp)
            )
        )

        FilledTonalIconButton(
            onClick = onDelete
        ) {
            Icon(
                imageVector = Icons.Rounded.Delete,
                contentDescription = "Удалить строку"
            )
        }
    }
}

@Composable
private fun PowerOfAttorneyActions(
    isEditing: Boolean,
    onSaveDocument: () -> Unit,
    onSaveAsNewDocument: () -> Unit,
    onDeleteDocument: () -> Unit,
    onExportDocument: () -> Unit,
    onPrintDocument: () -> Unit,
    modifier: Modifier = Modifier,
) {
    FlowRow(
        modifier = modifier.fillMaxWidth().animateContentSize(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        itemVerticalAlignment = Alignment.CenterVertically
    ) {
        Button(onClick = onSaveDocument) {
            Text("Сохранить")
        }

        if (isEditing) {
            OutlinedButton(onClick = onSaveAsNewDocument) {
                Text("Сохранить как новый")
            }

            OutlinedButton(onClick = onDeleteDocument) {
                Text("Удалить")
            }
        }

        Button(onClick = onExportDocument) {
            Text("Экспорт в файл")
        }

        Button(onClick = onPrintDocument) {
            Text("Печать")
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun LoadingContent() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        ContainedLoadingIndicator(modifier = Modifier.size(128.dp))
    }
}