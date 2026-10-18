/*
 * Copyright (c) 2026 Andrew Z.
 * docmanager — a scalable document management system.
 */

package ru.example.docmanager.ui.viewmodel.forms.powerofattorney

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.example.docmanager.data.document.Document
import ru.example.docmanager.data.document.type.powerofattorney.PowerOfAttorney
import ru.example.docmanager.data.repositories.documents.PowerOfAttorneyRepository
import ru.example.docmanager.data.repositories.references.IndividualsRepository
import ru.example.docmanager.data.repositories.references.OrganizationsRepository
import ru.example.docmanager.data.repositories.references.ProductsRepository
import ru.example.docmanager.data.repositories.references.SuppliersRepository
import ru.example.docmanager.di.DocumentProcessor
import ru.example.docmanager.di.getDocumentProcessor

class PowerOfAttorneyViewModel(
    val repository: PowerOfAttorneyRepository,
    val individualsRepository: IndividualsRepository,
    val organizationsRepository: OrganizationsRepository,
    val productsRepository: ProductsRepository,
    val suppliersRepository: SuppliersRepository
) : ViewModel() {
    private val documentProcessor: DocumentProcessor = getDocumentProcessor()

    val uiState: StateFlow<PowerOfAttorneyUiState>
        field = MutableStateFlow(PowerOfAttorneyUiState())

    private val _documents =
        MutableStateFlow<List<PowerOfAttorney>>(emptyList())
    val documents: StateFlow<List<PowerOfAttorney>> =
        _documents.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun loadInitialData(document: Document? = null) {
        viewModelScope.launch {
            loadInitialDataSuspend(document)
        }
    }

    suspend fun loadInitialDataSuspend(document: Document? = null) {
        uiState.value = uiState.value.copy(
            individuals = individualsRepository.getItems(),
            suppliers = suppliersRepository.getItems(),
            organizations = organizationsRepository.getItems(),
            products = productsRepository.getItems()
        )

        reloadDocumentsSuspend()

        if (document != null) {
            if (document is PowerOfAttorney) {
                uiState.value.populateFromDocument(document)
            } else {
                uiState.value.statusMessage = "Ошибка при загрузке документа"
            }
        } else {
            uiState.value.resetForm()
        }
    }

    fun reloadDocuments() {
        viewModelScope.launch {
            reloadDocumentsSuspend()
        }
    }

    suspend fun reloadDocumentsSuspend() {
        _isLoading.value = true
        runCatching {
            val list = repository.getAllDocuments()
            _documents.value = list
            uiState.value.existingDocuments = list
        }.onFailure { e ->
            uiState.value.statusMessage = "Ошибка при загрузке документов: ${e.message}"
        }
        _isLoading.value = false
    }

    fun saveDocument(onDocumentSaved: (() -> Unit)? = null) {
        viewModelScope.launch {
            saveDocumentSuspend(onDocumentSaved)
        }
    }

    suspend fun saveDocumentSuspend(onDocumentSaved: (() -> Unit)? = null) {
        val document = uiState.value.toDocument()
        if (document == null) {
            uiState.value.statusMessage = "Пожалуйста, выберите организацию, физ. лицо и поставщика"
            return
        }

        _isLoading.value = true
        runCatching {
            if (uiState.value.isEditing) {
                repository.updateDocument(document)
                uiState.value.statusMessage = "Документ № ${document.header.number} успешно обновлен"
            } else {
                repository.createDocument(document)
                uiState.value.statusMessage = "Документ № ${document.header.number} успешно сохранен в базу данных"
            }
            reloadDocumentsSuspend()
            onDocumentSaved?.invoke()
        }.onFailure { e ->
            uiState.value.statusMessage = "Ошибка при сохранении: ${e.message}"
        }
        _isLoading.value = false
    }

    fun saveAsNewDocument(onDocumentSaved: (() -> Unit)? = null) {
        viewModelScope.launch {
            saveAsNewDocumentSuspend(onDocumentSaved)
        }
    }

    suspend fun saveAsNewDocumentSuspend(onDocumentSaved: (() -> Unit)? = null) {
        val document = uiState.value.toDocument()
        if (document == null) {
            uiState.value.statusMessage = "Пожалуйста, выберите организацию, физ. лицо и поставщика"
            return
        }

        _isLoading.value = true
        runCatching {
            repository.createDocument(document)
            uiState.value.statusMessage = "Документ № ${document.header.number} сохранен как новый"
            uiState.value.selectedDocumentId = null
            reloadDocumentsSuspend()
            onDocumentSaved?.invoke()
        }.onFailure { e ->
            uiState.value.statusMessage = "Ошибка при сохранении: ${e.message}"
        }
        _isLoading.value = false
    }

    fun deleteCurrentDocument(onDocumentDeleted: (() -> Unit)? = null) {
        viewModelScope.launch {
            deleteCurrentDocumentSuspend(onDocumentDeleted)
        }
    }

    suspend fun deleteCurrentDocumentSuspend(onDocumentDeleted: (() -> Unit)? = null) {
        val docId = uiState.value.selectedDocumentId ?: return
        _isLoading.value = true
        runCatching {
            repository.deleteDocument(docId)
            uiState.value.statusMessage = "Документ удален из базы данных"
            resetForm()
            reloadDocumentsSuspend()
            onDocumentDeleted?.invoke()
        }.onFailure { e ->
            uiState.value.statusMessage = "Ошибка при удалении: ${e.message}"
        }
        _isLoading.value = false
    }

    fun exportToFile() {
        val document = uiState.value.toDocument()
        if (document == null) {
            uiState.value.statusMessage = "Ошибка при сохранении в файл: Документ не может быть сохранен"
            return
        }

        viewModelScope.launch {
            runCatching {
                documentProcessor.processSave(
                    documentName = "Доверенность",
                    documentResourceFile = "Доверенность.docx",
                    document = document
                )
                uiState.value.statusMessage = "Документ сохранен в файл"
            }.onFailure { e ->
                uiState.value.statusMessage = "Ошибка при сохранении в файл: ${e.message}"
            }
        }
    }

    fun printDocument() {
        val document = uiState.value.toDocument()
        if (document == null) {
            uiState.value.statusMessage = "Ошибка при сохранении в файл: Документ не может быть распечатан"
            return
        }

        viewModelScope.launch {
            runCatching {
                documentProcessor.processPrint(
                    documentName = "Доверенность",
                    documentResourceFile = "Доверенность.docx",
                    document = document
                )
                uiState.value.statusMessage = "Запрос на печать документа отправлен"
            }.onFailure { e ->
                uiState.value.statusMessage = "Ошибка при формировании документа для печати: ${e.message}"
            }
        }
    }

    fun resetForm() {
        uiState.value.resetForm()
    }

    fun autoPopulateDefaults() {
        if (uiState.value.isEditing) return
        if (uiState.value.selectedOrganization == null && uiState.value.organizations.isNotEmpty()) {
            uiState.value.selectedOrganization = uiState.value.organizations.first()
        }
        if (uiState.value.selectedIndividual == null && uiState.value.individuals.isNotEmpty()) {
            uiState.value.selectedIndividual = uiState.value.individuals.first()
        }
        if (uiState.value.selectedSupplier == null && uiState.value.suppliers.isNotEmpty()) {
            uiState.value.selectedSupplier = uiState.value.suppliers.first()
        }
        if (uiState.value.products.isNotEmpty()) {
            uiState.value.bodyItems = uiState.value.bodyItems.map { item ->
                if (item.product == null) item.copy(product = uiState.value.products.first()) else item
            }
        }
    }
}
