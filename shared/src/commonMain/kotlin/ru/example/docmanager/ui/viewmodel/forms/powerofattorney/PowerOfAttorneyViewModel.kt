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
    private val repository: PowerOfAttorneyRepository,
    private val individualsRepository: IndividualsRepository,
    private val organizationsRepository: OrganizationsRepository,
    private val productsRepository: ProductsRepository,
    private val suppliersRepository: SuppliersRepository
) : ViewModel() {
    companion object {
        const val DOCUMENT_NAME = "Доверенность"
        const val DOCUMENT_FILE = "Доверенность.docx"

        const val FAILED_INITIAL_LOADING = "Ошибка при загрузке документа"

        const val FAILED_SAVING = "Ошибка при сохранении"
        const val SAVED = "Документ № %d сохранен"
        const val SAVED_AS_NEW = "Документ № %d сохранен как новый"

        const val FAILED_SAVING_FILE = "Ошибка при сохранении файла"
        const val SAVED_FILE = "Документ сохранен в файл"

        const val FAILED_DELETING = "Ошибка при удалении"
        const val DELETED = "Документ удален из базы данных"


        const val FAILED_PRINTING = "Ошибка при печати"
        const val PRINTED = "Документ распечатан"
    }

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

        when (document) {
            null -> Unit
            is PowerOfAttorney -> {
                uiState.value.populateFromDocument(document)
            }
            else -> {
                setStatus(FAILED_INITIAL_LOADING)
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
        isLoading.value = true
        val document = uiState.value.toDocument() ?: return

        runCatching {
            if (uiState.value.isEditing) {
                repository.updateDocument(document)
            } else {
                uiState.value.selectedDocumentId = repository.createDocument(document)
            }
            setStatus(SAVED.format(document.header.number))
            onDocumentSaved?.invoke()
        }.onFailure { e ->
            setStatus(FAILED_SAVING)
            e.printStackTrace()
        }
        isLoading.value = false
    }

    fun onSaveAsNewDocument(
        onDocumentSaved: (() -> Unit)? = null
    ) {
        viewModelScope.launch {
            saveAsNewDocumentSuspend(onDocumentSaved)
        }
    }

    suspend fun saveAsNewDocumentSuspend(
        onDocumentSaved: (() -> Unit)? = null
    ) {
        isLoading.value = true
        val document = uiState.value.toDocument() ?: return

        runCatching {
            println("Saving document: ${uiState.value.selectedDocumentId}")
            uiState.value.selectedDocumentId = repository.createDocument(document)
            println("Now at: ${uiState.value.selectedDocumentId}")
            setStatus(SAVED_AS_NEW.format(document.header.number))
            onDocumentSaved?.invoke()
        }.onFailure { e ->
            setStatus(FAILED_SAVING)
            e.printStackTrace()
        }
        isLoading.value = false
    }

    fun onDeleteCurrentDocument(
        onDocumentDeleted: (() -> Unit)? = null
    ) {
        viewModelScope.launch {
            deleteCurrentDocumentSuspend(onDocumentDeleted)
        }
    }

    fun onExportToFile() {
        isLoading.value = true
        val document = uiState.value.toDocument() ?: return

        viewModelScope.launch {
            runCatching {
                documentProcessor.processSave(
                    documentName = DOCUMENT_NAME,
                    documentResourceFile = DOCUMENT_FILE,
                    document = document
                )
                setStatus(SAVED_FILE)
            }.onFailure { e ->
                setStatus(FAILED_SAVING_FILE)
                e.printStackTrace()
            }
            isLoading.value = false
        }
    }

    suspend fun deleteCurrentDocumentSuspend(
        onDocumentDeleted: (() -> Unit)? = null
    ) {
        isLoading.value = true
        val docId = uiState.value.selectedDocumentId ?: return
        runCatching {
            uiState.value.selectedDocumentId = null
            repository.deleteDocument(docId)
            setStatus(DELETED)
            onDocumentDeleted?.invoke()
        }.onFailure { e ->
            setStatus(FAILED_DELETING)
            e.printStackTrace()
        }
        isLoading.value = false
    }

    fun onPrintDocument() {
        isLoading.value = true
        val document = uiState.value.toDocument() ?: return

        viewModelScope.launch {
            runCatching {
                documentProcessor.processPrint(
                    documentName = DOCUMENT_NAME,
                    documentResourceFile = DOCUMENT_FILE,
                    document = document
                )
                setStatus(PRINTED)
            }.onFailure { e ->
                setStatus(FAILED_PRINTING)
                e.printStackTrace()
            }
            isLoading.value = false
        }
    }

    private fun setStatus(message: String) {
        uiState.value.statusMessage = message
    }
}
