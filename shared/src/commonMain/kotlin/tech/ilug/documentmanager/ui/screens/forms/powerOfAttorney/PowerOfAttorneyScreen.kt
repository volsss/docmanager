package tech.ilug.documentmanager.ui.screens.forms.powerOfAttorney

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.window.core.layout.WindowSizeClass
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.todayIn
import tech.ilug.documentmanager.di.getDocumentProcessor
import tech.ilug.documentmanager.model.PowerOfAttorney
import tech.ilug.documentmanager.repository.documents.PowerOfAttorneyRepository
import tech.ilug.documentmanager.ui.screens.DATE_FORMAT
import tech.ilug.documentmanager.ui.screens.forms.ReferenceDropdown
import tech.ilug.documentmanager.viewmodel.ReferenceViewModel
import kotlin.time.Clock

@Composable
fun rememberPowerOfAttorneyFormState(): PowerOfAttorneyFormState {
    val today = remember { Clock.System.todayIn(TimeZone.currentSystemDefault()) }
    val defaultEndDate = remember { today.plus(DatePeriod(days = 10)) }
    return remember { PowerOfAttorneyFormState(today, defaultEndDate) }
}

@Composable
fun PowerOfAttorneyScreen(
    referenceViewModel: ReferenceViewModel
) {
    val scope = rememberCoroutineScope()
    val powerOfAttorneyRepository = remember {
        PowerOfAttorneyRepository(
            referenceViewModel.individualsRepository,
            referenceViewModel.organizationsRepository,
            referenceViewModel.productsRepository,
            referenceViewModel.suppliersRepository
        )
    }

    val referencesItems by referenceViewModel.referencesItems.collectAsState()
    val organizations = remember(referencesItems) {
        referencesItems?.get(referenceViewModel.organizationsRepository)?.filterIsInstance<PowerOfAttorney.Organization>().orEmpty()
    }
    val individuals = remember(referencesItems) {
        referencesItems?.get(referenceViewModel.individualsRepository)?.filterIsInstance<PowerOfAttorney.Individual>().orEmpty()
    }
    val suppliers = remember(referencesItems) {
        referencesItems?.get(referenceViewModel.suppliersRepository)?.filterIsInstance<PowerOfAttorney.Supplier>().orEmpty()
    }
    val products = remember(referencesItems) {
        referencesItems?.get(referenceViewModel.productsRepository)?.filterIsInstance<PowerOfAttorney.Product>().orEmpty()
    }

    val formState = rememberPowerOfAttorneyFormState()

    fun reloadDocuments() {
        scope.launch {
            runCatching { formState.existingDocuments = powerOfAttorneyRepository.getAllDocuments() }
        }
    }

    LaunchedEffect(Unit) {
        if (referenceViewModel.referencesItems.value == null) {
            referenceViewModel.loadReferenceItems()
        }
        reloadDocuments()
    }

    LaunchedEffect(organizations) {
        if (formState.selectedOrganization == null && organizations.isNotEmpty()) {
            formState.selectedOrganization = organizations.first()
        }
    }
    LaunchedEffect(individuals) {
        if (formState.selectedIndividual == null && individuals.isNotEmpty()) {
            formState.selectedIndividual = individuals.first()
        }
    }
    LaunchedEffect(suppliers) {
        if (formState.selectedSupplier == null && suppliers.isNotEmpty()) {
            formState.selectedSupplier = suppliers.first()
        }
    }
    LaunchedEffect(products) {
        if (products.isNotEmpty()) {
            formState.bodyItems = formState.bodyItems.map { item ->
                if (item.product == null) item.copy(product = products.first()) else item
            }
        }
    }

    Column(
        modifier = Modifier.widthIn(max = 800.dp).fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        PowerOfAttorneyBanner(
            formState = formState,
            onResetForm = { formState.resetForm(organizations, individuals, suppliers, products) }
        )

        Spacer(Modifier.height(16.dp))

        PowerOfAttorneyHeaderSection(
            formState = formState,
            organizations = organizations,
            individuals = individuals,
            suppliers = suppliers,
            products = products,
            onResetForm = { formState.resetForm(organizations, individuals, suppliers, products) }
        )

        Spacer(Modifier.height(8.dp))

        PowerOfAttorneyBodySection(formState = formState, products = products)

        Spacer(Modifier.height(8.dp))

        PowerOfAttorneyActions(
            formState = formState,
            scope = scope,
            repository = powerOfAttorneyRepository,
            products = products,
            onDocumentChanged = ::reloadDocuments,
            onResetForm = { formState.resetForm(organizations, individuals, suppliers, products) }
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
        Text("Доверенность", style = MaterialTheme.typography.titleLarge)

        formState.statusMessage?.let { msg ->
            Text(
                text = msg,
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.bodyMedium
            )
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
    organizations: List<PowerOfAttorney.Organization>,
    individuals: List<PowerOfAttorney.Individual>,
    suppliers: List<PowerOfAttorney.Supplier>,
    products: List<PowerOfAttorney.Product>,
    onResetForm: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Шапка документа",
            modifier = Modifier.fillMaxWidth(),
            style = MaterialTheme.typography.titleMedium
        )

        DocumentNumberDropdown(
            number = formState.number,
            onNumberChange = { formState.number = it },
            expanded = formState.numberDropdownExpanded,
            onExpandedChange = { formState.numberDropdownExpanded = it },
            existingDocuments = formState.existingDocuments,
            onNewDocumentClick = onResetForm,
            onDocumentSelected = { doc ->
                formState.populateFromDocument(doc, organizations, individuals, suppliers, products)
                formState.numberDropdownExpanded = false
            }
        )

        OutlinedTextField(
            value = formState.dischargeDate,
            onValueChange = { formState.dischargeDate = it },
            label = { Text("Дата выписки (ДД.ММ.ГГГГ)") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = formState.endDate,
            onValueChange = { formState.endDate = it },
            label = { Text("Дата окончания (ДД.ММ.ГГГГ)") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        ReferenceDropdown(
            label = "Организация",
            items = organizations,
            selectedItem = formState.selectedOrganization,
            onItemSelected = { formState.selectedOrganization = it },
            itemLabel = { it.name.ifBlank { "Организация #${it.id}" } },
            modifier = Modifier.fillMaxWidth()
        )

        ReferenceDropdown(
            label = "Физ. лицо",
            items = individuals,
            selectedItem = formState.selectedIndividual,
            onItemSelected = { formState.selectedIndividual = it },
            itemLabel = {
                val parts = listOfNotNull(it.name.ifBlank { null }, it.job.ifBlank { null })
                if (parts.isNotEmpty()) parts.joinToString(" - ") else "Физ. лицо #${it.id}"
            },
            modifier = Modifier.fillMaxWidth()
        )

        ReferenceDropdown(
            label = "На получение от (Поставщик)",
            items = suppliers,
            selectedItem = formState.selectedSupplier,
            onItemSelected = { formState.selectedSupplier = it },
            itemLabel = { it.name.ifBlank { "Поставщик #${it.id}" } },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = formState.supplierAgreement,
            onValueChange = { formState.supplierAgreement = it },
            label = { Text("Материальных ценностей по") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun PowerOfAttorneyBodySection(
    formState: PowerOfAttorneyFormState,
    products: List<PowerOfAttorney.Product>,
    modifier: Modifier = Modifier
) {
    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Тело документа (Материальные ценности)",
            modifier = Modifier.fillMaxWidth(),
            style = MaterialTheme.typography.titleMedium
        )

        Column(
            Modifier.fillMaxSize().then(
                if (windowSizeClass.isWidthAtLeastBreakpoint(840)) Modifier else Modifier.horizontalScroll(rememberScrollState())
            )
        ) {
            formState.bodyItems.forEachIndexed { index, item ->
                BodyItemRow(
                    index = index,
                    item = item,
                    products = products,
                    onItemChange = { updated -> formState.updateBodyRow(index, updated) },
                    onDelete = { formState.removeBodyRow(index, products.firstOrNull()) },
                    windowSizeClass = windowSizeClass
                )
            }
        }

        OutlinedButton(
            onClick = { formState.addBodyRow(products.firstOrNull()) },
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
    item: PowerOfAttorneyBodyItem,
    products: List<PowerOfAttorney.Product>,
    onItemChange: (PowerOfAttorneyBodyItem) -> Unit,
    onDelete: () -> Unit,
    windowSizeClass: WindowSizeClass
) {
    val isWide = windowSizeClass.isWidthAtLeastBreakpoint(840)

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
            label = "Материальные ценности",
            items = products,
            selectedItem = item.product,
            onItemSelected = { onItemChange(item.copy(product = it)) },
            itemLabel = { it.name.ifBlank { "Товар #${it.id}" } },
            modifier = if (isWide) Modifier.weight(2f) else Modifier.widthIn(min = 300.dp)
        )

        OutlinedTextField(
            value = item.unit,
            onValueChange = { onItemChange(item.copy(unit = it)) },
            label = { Text("Ед. изм.") },
            singleLine = true,
            modifier = if (isWide) Modifier.weight(1f) else Modifier.widthIn(min = 150.dp)
        )

        OutlinedTextField(
            value = item.count,
            onValueChange = { onItemChange(item.copy(count = it)) },
            label = { Text("Количество (прописью)") },
            singleLine = true,
            modifier = if (isWide) Modifier.weight(1.5f) else Modifier.widthIn(min = 300.dp)
        )

        IconButton(onClick = onDelete) {
            Icon(Icons.Default.Delete, contentDescription = "Удалить строку")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DocumentNumberDropdown(
    number: String,
    onNumberChange: (String) -> Unit,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    existingDocuments: List<PowerOfAttorney>,
    onNewDocumentClick: () -> Unit,
    onDocumentSelected: (PowerOfAttorney) -> Unit,
    modifier: Modifier = Modifier
) {
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = onExpandedChange,
        modifier = modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = number,
            onValueChange = onNumberChange,
            label = { Text("Номер документа (выберите из списка или введите)") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryEditable)
                .fillMaxWidth(),
            singleLine = true
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { onExpandedChange(false) }
        ) {
            DropdownMenuItem(
                text = { Text("Создать новый документ") },
                onClick = {
                    onNewDocumentClick()
                    onExpandedChange(false)
                }
            )
            if (existingDocuments.isEmpty()) {
                DropdownMenuItem(
                    text = { Text("Нет сохраненных документов в базе") },
                    onClick = {},
                    enabled = false
                )
            } else {
                existingDocuments.forEach { doc ->
                    val orgName = doc.header.organization.name.ifBlank { "Организация #${doc.header.organization.id}" }
                    DropdownMenuItem(
                        text = {
                            Column {
                                Text(
                                    "№ ${doc.header.number} от ${DATE_FORMAT.format(doc.header.dischargeDate)}",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Text(
                                    orgName,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        },
                        onClick = { onDocumentSelected(doc) }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun PowerOfAttorneyActions(
    formState: PowerOfAttorneyFormState,
    scope: CoroutineScope,
    repository: PowerOfAttorneyRepository,
    products: List<PowerOfAttorney.Product>,
    onDocumentChanged: () -> Unit,
    onResetForm: () -> Unit,
    modifier: Modifier = Modifier
) {
    FlowRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        itemVerticalAlignment = Alignment.CenterVertically
    ) {
        Button(
            onClick = {
                val document = formState.toPowerOfAttorney(
                    headerId = formState.selectedDocumentId ?: 0,
                    defaultProduct = products.firstOrNull()
                )
                if (document == null) {
                    formState.statusMessage = "Пожалуйста, выберите организацию, физ. лицо и поставщика"
                    return@Button
                }

                scope.launch {
                    try {
                        if (formState.isEditing) {
                            repository.updateDocument(document)
                            formState.statusMessage = "Документ № ${document.header.number} успешно обновлен"
                        } else {
                            repository.createDocument(document)
                            formState.statusMessage = "Документ № ${document.header.number} успешно сохранен в базу данных"
                        }
                        onDocumentChanged()
                    } catch (e: Exception) {
                        formState.statusMessage = "Ошибка при сохранении: ${e.message}"
                    }
                }
            }
        ) {
            Text(if (formState.isEditing) "Сохранить (обновить)" else "Сохранить")
        }

        if (formState.isEditing) {
            OutlinedButton(
                onClick = {
                    val document = formState.toPowerOfAttorney(
                        headerId = 0,
                        defaultProduct = products.firstOrNull()
                    )
                    if (document == null) {
                        formState.statusMessage = "Пожалуйста, выберите организацию, физ. лицо и поставщика"
                        return@OutlinedButton
                    }

                    scope.launch {
                        try {
                            repository.createDocument(document)
                            formState.statusMessage = "Документ № ${document.header.number} сохранен как новый"
                            formState.selectedDocumentId = null
                            onDocumentChanged()
                        } catch (e: Exception) {
                            formState.statusMessage = "Ошибка при сохранении: ${e.message}"
                        }
                    }
                }
            ) {
                Text("Сохранить как новый")
            }

            OutlinedButton(
                onClick = {
                    val docId = formState.selectedDocumentId ?: return@OutlinedButton
                    scope.launch {
                        try {
                            repository.deleteDocument(docId)
                            formState.statusMessage = "Документ удален из базы данных"
                            onResetForm()
                            onDocumentChanged()
                        } catch (e: Exception) {
                            formState.statusMessage = "Ошибка при удалении: ${e.message}"
                        }
                    }
                }
            ) {
                Text("Удалить")
            }
        }

        Button(
            onClick = {
                try {
                    val (head, body) = formState.prepareExportData()
                    getDocumentProcessor().processSave(
                        documentName = "Доверенность",
                        documentResourceFile = "Доверенность.docx",
                        headReplacements = head,
                        bodyParts = body
                    )
                    formState.statusMessage = "Документ сохранен в файл"
                } catch (e: Exception) {
                    formState.statusMessage = "Ошибка при сохранении в файл: ${e.message}"
                }
            }
        ) {
            Text("Сохранить в файл")
        }

        Button(
            onClick = {
                try {
                    val (head, body) = formState.prepareExportData()
                    getDocumentProcessor().processPrint(
                        documentName = "Доверенность",
                        documentResourceFile = "Доверенность.docx",
                        headReplacements = head,
                        bodyParts = body
                    )
                    formState.statusMessage = "Запрос на печать документа отправлен"
                } catch (e: Exception) {
                    formState.statusMessage = "Ошибка при формировании документа для печати: ${e.message}"
                }
            }
        ) {
            Text("Печать")
        }
    }
}
