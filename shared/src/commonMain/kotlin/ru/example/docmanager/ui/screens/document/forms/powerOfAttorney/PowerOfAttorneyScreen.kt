/*
 * Copyright (c) 2026 Andrew Z.
 * docmanager — a scalable document management system.
 */

package ru.example.docmanager.ui.screens.document.forms.powerOfAttorney

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfoV2
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import docmanager.shared.generated.resources.*
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import ru.example.docmanager.data.document.header.HeaderFieldType
import ru.example.docmanager.data.reference.Individual
import ru.example.docmanager.data.reference.Organization
import ru.example.docmanager.data.reference.Product
import ru.example.docmanager.data.reference.Reference
import ru.example.docmanager.data.reference.ReferenceType
import ru.example.docmanager.data.reference.Supplier
import ru.example.docmanager.ui.Utils
import ru.example.docmanager.ui.components.TextInput
import ru.example.docmanager.ui.screens.document.forms.ReferenceDropdown
import ru.example.docmanager.ui.viewmodel.PowerOfAttorneyViewModel

@Composable
fun PowerOfAttorneyScreen(
    viewModel: PowerOfAttorneyViewModel = koinInject()
) {
    val formState = viewModel.formState
    val referenceUiState by viewModel.referenceViewModel.uiState.collectAsStateWithLifecycle()
    val referencesItems = referenceUiState.itemsByType

    val organizations = remember(referencesItems) {
        referencesItems[ReferenceType.ORGANIZATION]?.filterIsInstance<Organization>()
            .orEmpty()
    }
    val individuals = remember(referencesItems) {
        referencesItems[ReferenceType.INDIVIDUAL]?.filterIsInstance<Individual>()
            .orEmpty()
    }
    val suppliers = remember(referencesItems) {
        referencesItems[ReferenceType.SUPPLIER]?.filterIsInstance<Supplier>()
            .orEmpty()
    }
    val products = remember(referencesItems) {
        referencesItems[ReferenceType.PRODUCT]?.filterIsInstance<Product>()
            .orEmpty()
    }

    LaunchedEffect(Unit) {
        viewModel.loadInitialData()
    }

    LaunchedEffect(organizations, individuals, suppliers, products) {
        viewModel.autoPopulateDefaults(organizations, individuals, suppliers, products)
    }

    Column(
        modifier = Modifier.widthIn(max = 800.dp).fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        PowerOfAttorneyBanner(
            formState = formState,
            onResetForm = viewModel::resetForm
        )

        PowerOfAttorneyHeaderSection(
            formState = formState,
            referencesItems = referencesItems,
            organizations = organizations,
            individuals = individuals,
            suppliers = suppliers,
            onResetForm = viewModel::resetForm
        )

        PowerOfAttorneyBodySection(formState = formState, products = products)

        PowerOfAttorneyActions(
            viewModel = viewModel,
            formState = formState
        )
    }
}

@Composable
private fun PowerOfAttorneyBanner(
    formState: PowerOfAttorneyFormState,
    onResetForm: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        AnimatedVisibility(visible = formState.statusMessage != null) {
            Text(
                text = formState.statusMessage ?: "",
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        if (formState.isEditing) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Редактируется документ № ${formState.number} (ID: ${formState.selectedDocumentId})",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
                OutlinedButton(
                    onClick = onResetForm,
                    modifier = Modifier.height(32.dp)
                ) {
                    Text("Создать новый", style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}

@Composable
private fun PowerOfAttorneyHeaderSection(
    formState: PowerOfAttorneyFormState,
    referencesItems: Map<ReferenceType, List<Reference>>,
    organizations: List<Organization>,
    individuals: List<Individual>,
    suppliers: List<Supplier>,
    onResetForm: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = stringResource(Res.string.header),
            modifier = Modifier.fillMaxWidth(),
            style = MaterialTheme.typography.titleMedium
        )

        TextInput(
            value = formState.number,
            onValueChange = { formState.number = it },
            label = stringResource(HeaderFieldType.DISCHARGE_DATE.stringResource)
        )

        OutlinedTextField(
            value = formState.dischargeDate,
            onValueChange = { formState.dischargeDate = it },
            label = { Text(stringResource(HeaderFieldType.DISCHARGE_DATE.stringResource)) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = formState.endDate,
            onValueChange = { formState.endDate = it },
            label = { Text(stringResource(HeaderFieldType.END_DATE.stringResource)) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        ReferenceDropdown(
            label = stringResource(Res.string.input_organization),
            items = organizations,
            selectedItem = formState.selectedOrganization,
            onItemSelected = { formState.selectedOrganization = it },
            itemLabel = { it.name.ifBlank { "№ ${it.id}" } },
            modifier = Modifier.fillMaxWidth()
        )

        ReferenceDropdown(
            label = stringResource(Res.string.input_individual),
            items = individuals,
            selectedItem = formState.selectedIndividual,
            onItemSelected = { formState.selectedIndividual = it },
            itemLabel = {
                val parts = listOfNotNull(it.name.ifBlank { null }, it.job.ifBlank { null })
                if (parts.isNotEmpty()) parts.joinToString(" - ") else "№ ${it.id}"
            },
            modifier = Modifier.fillMaxWidth()
        )

        ReferenceDropdown(
            label = stringResource(Res.string.input_supplier),
            items = suppliers,
            selectedItem = formState.selectedSupplier,
            onItemSelected = { formState.selectedSupplier = it },
            itemLabel = { it.name.ifBlank { "№ ${it.id}" } },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = formState.supplierAgreement,
            onValueChange = { formState.supplierAgreement = it },
            label = { Text(stringResource(HeaderFieldType.SUPPLIER_AGREEMENT.stringResource)) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun PowerOfAttorneyBodySection(
    formState: PowerOfAttorneyFormState,
    products: List<Product>,
    modifier: Modifier = Modifier
) {
    val windowSizeClass = currentWindowAdaptiveInfoV2().windowSizeClass
    val isWide = windowSizeClass.isWidthAtLeastBreakpoint(Utils.WIDE_BREAKPOINT)

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = stringResource(Res.string.body),
            modifier = Modifier.fillMaxWidth(),
            style = MaterialTheme.typography.titleMedium
        )

        Column(
            Modifier.fillMaxSize().then(
                if (isWide) Modifier else Modifier.horizontalScroll(rememberScrollState())
            )
        ) {
            formState.bodyItems.forEachIndexed { index, item ->
                BodyItemRow(
                    index = index,
                    item = item,
                    products = products,
                    onItemChange = { updated -> formState.updateBodyItem(index, updated) },
                    onDelete = { formState.removeBodyItem(index) },
                    isWide = isWide
                )
            }
        }

        OutlinedButton(
            onClick = { formState.addBodyItem() },
            modifier = Modifier.align(Alignment.Start)
        ) {
            Icon(Icons.Default.Add, contentDescription = null)
            Spacer(Modifier.width(4.dp))
            Text("Добавить строку")
        }
    }
}

@Composable
private fun BodyItemRow(
    index: Int,
    item: PowerOfAttorneyBodyItemState,
    products: List<Product>,
    onItemChange: (PowerOfAttorneyBodyItemState) -> Unit,
    onDelete: () -> Unit,
    isWide: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "${index + 1}",
            modifier = Modifier.width(24.dp),
            style = MaterialTheme.typography.bodyMedium
        )

        ReferenceDropdown(
            label = stringResource(Res.string.input_product),
            items = products,
            selectedItem = item.product,
            onItemSelected = { onItemChange(item.copy(product = it)) },
            itemLabel = { it.name.ifBlank { "№ ${it.id}" } },
            modifier = if (isWide) Modifier.weight(2f) else Modifier.widthIn(min = 300.dp)
        )

        OutlinedTextField(
            value = item.unit,
            onValueChange = { onItemChange(item.copy(unit = it)) },
            label = { Text(stringResource(Res.string.body_field_unit)) },
            singleLine = true,
            modifier = if (isWide) Modifier.weight(1f) else Modifier.widthIn(min = 150.dp)
        )

        OutlinedTextField(
            value = item.count,
            onValueChange = { onItemChange(item.copy(count = it)) },
            label = { Text(stringResource(Res.string.body_field_count)) },
            singleLine = true,
            modifier = if (isWide) Modifier.weight(1.5f) else Modifier.widthIn(min = 300.dp)
        )

        IconButton(onClick = onDelete) {
            Icon(Icons.Default.Delete, contentDescription = "Удалить строку")
        }
    }
}

@Composable
private fun PowerOfAttorneyActions(
    viewModel: PowerOfAttorneyViewModel,
    formState: PowerOfAttorneyFormState,
    modifier: Modifier = Modifier
) {
    FlowRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        itemVerticalAlignment = Alignment.CenterVertically
    ) {
        Button(
            onClick = { viewModel.saveDocument() }
        ) {
            Text(if (formState.isEditing) "Сохранить (обновить)" else "Сохранить")
        }

        if (formState.isEditing) {
            OutlinedButton(
                onClick = { viewModel.saveAsNewDocument() }
            ) {
                Text("Сохранить как новый")
            }

            OutlinedButton(
                onClick = { viewModel.deleteCurrentDocument() }
            ) {
                Text("Удалить")
            }
        }

        Button(
            onClick = { viewModel.exportToFile() }
        ) {
            Text("Сохранить в файл")
        }

        Button(
            onClick = { viewModel.printDocument() }
        ) {
            Text("Печать")
        }
    }
}
