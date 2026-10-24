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

package ru.example.docmanager.ui.viewmodel.forms.powerofattorney

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
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

    val isLoading: StateFlow<Boolean>
        field = MutableStateFlow(true)

    fun loadInitialData(document: Document? = null) {
        viewModelScope.launch {
            loadInitialDataSuspend(document)
        }
    }

    suspend fun loadInitialDataSuspend(document: Document? = null) {
        isLoading.value = true
        uiState.value = uiState.value.copy(
            individuals = individualsRepository.getItems(),
            suppliers = suppliersRepository.getItems(),
            organizations = organizationsRepository.getItems(),
            products = productsRepository.getItems()
        )

        if (document != null) {
            if (document is PowerOfAttorney) {
                uiState.value.populateFromDocument(document)
            } else {
                uiState.value.statusMessage = "Ошибка при загрузке документа"
            }
        }
        isLoading.value = false
    }

    fun onSaveDocument(onDocumentSaved: (() -> Unit)? = null) {
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

        isLoading.value = true
        runCatching {
            if (uiState.value.isEditing) {
                repository.updateDocument(document)
                uiState.value.statusMessage = "Документ № ${document.header.number} успешно обновлен"
            } else {
                uiState.value.selectedDocumentId = repository.createDocument(document)
                uiState.value.statusMessage = "Документ № ${document.header.number} успешно сохранен в базу данных"
            }
            onDocumentSaved?.invoke()
        }.onFailure { e ->
            uiState.value.statusMessage = "Ошибка при сохранении: ${e.message}"
        }
        isLoading.value = false
    }

    fun onSaveAsNewDocument(onDocumentSaved: (() -> Unit)? = null) {
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

        isLoading.value = true
        runCatching {
            uiState.value.selectedDocumentId = repository.createDocument(document)
            uiState.value.statusMessage = "Документ № ${document.header.number} сохранен как новый"
            uiState.value.selectedDocumentId = null
            onDocumentSaved?.invoke()
        }.onFailure { e ->
            uiState.value.statusMessage = "Ошибка при сохранении: ${e.message}"
        }
        isLoading.value = false
    }

    fun onDeleteCurrentDocument(onDocumentDeleted: (() -> Unit)? = null) {
        viewModelScope.launch {
            deleteCurrentDocumentSuspend(onDocumentDeleted)
        }
    }

    suspend fun deleteCurrentDocumentSuspend(onDocumentDeleted: (() -> Unit)? = null) {
        val docId = uiState.value.selectedDocumentId ?: return
        isLoading.value = true
        runCatching {
            repository.deleteDocument(docId)
            uiState.value.statusMessage = "Документ удален из базы данных"
            onDocumentDeleted?.invoke()
        }.onFailure { e ->
            uiState.value.statusMessage = "Ошибка при удалении: ${e.message}"
        }
        isLoading.value = false
    }

    fun onExportToFile() {
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

    fun onPrintDocument() {
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
}
