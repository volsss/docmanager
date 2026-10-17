/*
 * Copyright (c) 2026 Andrew Z.
 * docmanager — a scalable document management system.
 */

package ru.example.docmanager.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import ru.example.docmanager.data.repositories.documents.DocumentRepository
import ru.example.docmanager.di.DocumentProcessor
import ru.example.docmanager.di.getDocumentProcessor
import ru.example.docmanager.ui.screens.document.forms.powerOfAttorney.PowerOfAttorneyFormState
import ru.example.docmanager.ui.viewmodel.reference.ReferenceViewModel
import kotlin.time.Clock

class PowerOfAttorneyViewModel(
    val repository: DocumentRepository<ru.example.docmanager.data.document.type.powerofattorney.PowerOfAttorney>,
    val referenceViewModel: ReferenceViewModel
) : ViewModel() {
    private val documentProcessor: DocumentProcessor = getDocumentProcessor()

    val formState = PowerOfAttorneyFormState(Clock.System.todayIn(TimeZone.currentSystemDefault()))

    private val _documents =
        MutableStateFlow<List<ru.example.docmanager.data.document.type.powerofattorney.PowerOfAttorney>>(emptyList())
    val documents: StateFlow<List<ru.example.docmanager.data.document.type.powerofattorney.PowerOfAttorney>> =
        _documents.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun loadInitialData(documentId: Int? = null) {
        viewModelScope.launch {
            loadInitialDataSuspend(documentId)
        }
    }

    suspend fun loadInitialDataSuspend(documentId: Int? = null) {
        if (referenceViewModel.formState.itemsByType.isEmpty()) {
            referenceViewModel.loadReferenceItemsSuspend()
        }
        reloadDocumentsSuspend()
        if (documentId != null) {
            runCatching {
                repository.getDocument(documentId)
            }.onSuccess { doc ->
                formState.populateFromDocument(doc, referenceViewModel.formState.itemsByType)
            }.onFailure { e ->
                formState.statusMessage = "Ошибка при загрузке документа: ${e.message}"
            }
        } else {
            formState.resetForm(referenceViewModel.formState.itemsByType)
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
            formState.existingDocuments = list
        }.onFailure { e ->
            formState.statusMessage = "Ошибка при загрузке документов: ${e.message}"
        }
        _isLoading.value = false
    }

    fun saveDocument(onDocumentSaved: (() -> Unit)? = null) {
        viewModelScope.launch {
            saveDocumentSuspend(onDocumentSaved)
        }
    }

    suspend fun saveDocumentSuspend(onDocumentSaved: (() -> Unit)? = null) {
        val document = formState.toDocument()
        if (document == null) {
            formState.statusMessage = "Пожалуйста, выберите организацию, физ. лицо и поставщика"
            return
        }

        _isLoading.value = true
        runCatching {
            if (formState.isEditing) {
                repository.updateDocument(document)
                formState.statusMessage = "Документ № ${document.header.number} успешно обновлен"
            } else {
                repository.createDocument(document)
                formState.statusMessage = "Документ № ${document.header.number} успешно сохранен в базу данных"
            }
            reloadDocumentsSuspend()
            onDocumentSaved?.invoke()
        }.onFailure { e ->
            formState.statusMessage = "Ошибка при сохранении: ${e.message}"
        }
        _isLoading.value = false
    }

    fun saveAsNewDocument(onDocumentSaved: (() -> Unit)? = null) {
        viewModelScope.launch {
            saveAsNewDocumentSuspend(onDocumentSaved)
        }
    }

    suspend fun saveAsNewDocumentSuspend(onDocumentSaved: (() -> Unit)? = null) {
        val document = formState.toDocument()
        if (document == null) {
            formState.statusMessage = "Пожалуйста, выберите организацию, физ. лицо и поставщика"
            return
        }

        _isLoading.value = true
        runCatching {
            repository.createDocument(document)
            formState.statusMessage = "Документ № ${document.header.number} сохранен как новый"
            formState.selectedDocumentId = null
            reloadDocumentsSuspend()
            onDocumentSaved?.invoke()
        }.onFailure { e ->
            formState.statusMessage = "Ошибка при сохранении: ${e.message}"
        }
        _isLoading.value = false
    }

    fun deleteCurrentDocument(onDocumentDeleted: (() -> Unit)? = null) {
        viewModelScope.launch {
            deleteCurrentDocumentSuspend(onDocumentDeleted)
        }
    }

    suspend fun deleteCurrentDocumentSuspend(onDocumentDeleted: (() -> Unit)? = null) {
        val docId = formState.selectedDocumentId ?: return
        _isLoading.value = true
        runCatching {
            repository.deleteDocument(docId)
            formState.statusMessage = "Документ удален из базы данных"
            resetForm()
            reloadDocumentsSuspend()
            onDocumentDeleted?.invoke()
        }.onFailure { e ->
            formState.statusMessage = "Ошибка при удалении: ${e.message}"
        }
        _isLoading.value = false
    }

    fun exportToFile() {
        val document = formState.toDocument()
        if (document == null) {
            formState.statusMessage = "Ошибка при сохранении в файл: Документ не может быть сохранен"
            return
        }

        viewModelScope.launch {
            runCatching {
                documentProcessor.processSave(
                    documentName = "Доверенность",
                    documentResourceFile = "Доверенность.docx",
                    document = document
                )
                formState.statusMessage = "Документ сохранен в файл"
            }.onFailure { e ->
                formState.statusMessage = "Ошибка при сохранении в файл: ${e.message}"
            }
        }
    }

    fun printDocument() {
        val document = formState.toDocument()
        if (document == null) {
            formState.statusMessage = "Ошибка при сохранении в файл: Документ не может быть распечатан"
            return
        }

        viewModelScope.launch {
            runCatching {
                documentProcessor.processPrint(
                    documentName = "Доверенность",
                    documentResourceFile = "Доверенность.docx",
                    document = document
                )
                formState.statusMessage = "Запрос на печать документа отправлен"
            }.onFailure { e ->
                formState.statusMessage = "Ошибка при формировании документа для печати: ${e.message}"
            }
        }
    }

    fun resetForm() {
        formState.resetForm(referenceViewModel.formState.itemsByType)
    }

    fun autoPopulateDefaults(
        organizations: List<ru.example.docmanager.data.reference.Organization>,
        individuals: List<ru.example.docmanager.data.reference.Individual>,
        suppliers: List<ru.example.docmanager.data.reference.Supplier>,
        products: List<ru.example.docmanager.data.reference.Product>
    ) {
        if (formState.isEditing) return
        if (formState.selectedOrganization == null && organizations.isNotEmpty()) {
            formState.selectedOrganization = organizations.first()
        }
        if (formState.selectedIndividual == null && individuals.isNotEmpty()) {
            formState.selectedIndividual = individuals.first()
        }
        if (formState.selectedSupplier == null && suppliers.isNotEmpty()) {
            formState.selectedSupplier = suppliers.first()
        }
        if (products.isNotEmpty()) {
            formState.bodyItems = formState.bodyItems.map { item ->
                if (item.product == null) item.copy(product = products.first()) else item
            }
        }
    }
}
