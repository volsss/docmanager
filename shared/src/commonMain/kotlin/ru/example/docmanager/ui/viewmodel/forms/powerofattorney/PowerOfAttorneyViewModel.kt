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

import androidx.lifecycle.viewModelScope
import docmanager.shared.generated.resources.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString
import ru.example.docmanager.data.document.Document
import ru.example.docmanager.data.document.type.powerofattorney.PowerOfAttorney
import ru.example.docmanager.data.repositories.documents.PowerOfAttorneyRepository
import ru.example.docmanager.data.repositories.references.IndividualsRepository
import ru.example.docmanager.data.repositories.references.OrganizationsRepository
import ru.example.docmanager.data.repositories.references.ProductsRepository
import ru.example.docmanager.data.repositories.references.SuppliersRepository
import ru.example.docmanager.ui.viewmodel.forms.FormViewModel


class PowerOfAttorneyViewModel(
    private val repository: PowerOfAttorneyRepository,
    private val individualsRepository: IndividualsRepository,
    private val organizationsRepository: OrganizationsRepository,
    private val productsRepository: ProductsRepository,
    private val suppliersRepository: SuppliersRepository
) : FormViewModel<PowerOfAttorney, PowerOfAttorneyUiState>() {
    companion object {
        val DOCUMENT_NAME = Res.string.document_power_of_attorney
        val DOCUMENT_RESOURCE = Res.string.document_power_of_attorney_resource
    }

    override val uiState: StateFlow<PowerOfAttorneyUiState>
        field = MutableStateFlow(PowerOfAttorneyUiState())

    override fun onInit(
        onStatusUpdate: (String) -> Unit,
        document: Document?,
    ) {
        viewModelScope.launch {
            uiState.value.isLoading = true
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
                    onStatusUpdate.invoke(
                        getString(Res.string.form_error_failed_init)
                    )
                }
            }
            uiState.value.isLoading = false
        }
    }

    override fun onSaveDocument(
        onStatusUpdate: (String) -> Unit,
        onDocumentUpdated: (() -> Unit)?,
        onDocumentSaved: ((PowerOfAttorney) -> Unit)?
    ) {
        viewModelScope.launch {
            uiState.value.isLoading = true
            val document = uiState.value.toDocument(onStatusUpdate)
                ?: return@launch

            runCatching {
                if (uiState.value.isEditing) {
                    repository.updateDocument(document)
                    onDocumentUpdated?.invoke()
                } else {
                    uiState.value.selectedDocumentId = null
                    onDocumentSaved?.invoke(repository.createDocument(document))
                }
                onStatusUpdate.invoke(getString(Res.string.form_saved))
            }.onFailure { e ->
                onStatusUpdate.invoke(getString(Res.string.form_error_failed_saving))
                e.printStackTrace()
            }
            uiState.value.isLoading = false
        }
    }

    override fun onSaveAsNewDocument(
        onStatusUpdate: (String) -> Unit,
        onDocumentSaved: ((PowerOfAttorney) -> Unit)?,
    ) {
        viewModelScope.launch {
            uiState.value.isLoading = true
            val document = uiState.value.toDocument(onStatusUpdate)
                ?: return@launch

            runCatching {
                val newDocument = repository.createDocument(document)
                uiState.value.selectedDocumentId = null
                onStatusUpdate.invoke(getString(Res.string.form_saved_as_new))
                onDocumentSaved?.invoke(newDocument)
            }.onFailure { e ->
                onStatusUpdate.invoke(getString(Res.string.form_error_failed_saving))
                e.printStackTrace()
            }
            uiState.value.isLoading = false
        }
    }

    override fun onDeleteDocument(
        onStatusUpdate: (String) -> Unit,
        onDocumentDeleted: (() -> Unit)?,
    ) {
        viewModelScope.launch {
            uiState.value.isLoading = true
            val docId = uiState.value.selectedDocumentId ?: return@launch
            runCatching {
                uiState.value.selectedDocumentId = null
                repository.deleteDocument(docId)
                onStatusUpdate.invoke(getString(Res.string.form_deleted))
                onDocumentDeleted?.invoke()
            }.onFailure { e ->
                onStatusUpdate.invoke(getString(Res.string.form_error_failed_deleting))
                e.printStackTrace()
            }
            uiState.value.isLoading = false
        }
    }

    override fun onExportToFile(
        onStatusUpdate: (String) -> Unit,
        onDocumentExported: (() -> Unit)?,
    ) {
        viewModelScope.launch {
            uiState.value.isLoading = true
            val document = uiState.value.toDocument(onStatusUpdate)
                ?: return@launch

            runCatching {
                documentProcessor.processSave(
                    documentName = getString(DOCUMENT_NAME)
                            + " № " + uiState.value.number,
                    documentResourceFile = getString(DOCUMENT_RESOURCE),
                    document = document
                )
                onStatusUpdate.invoke(getString(Res.string.form_exported))
            }.onFailure { e ->
                onStatusUpdate.invoke(getString(Res.string.form_error_failed_exporting))
                e.printStackTrace()
            }
            uiState.value.isLoading = false
        }
    }

    override fun onPrintDocument(
        onStatusUpdate: (String) -> Unit,
        onDocumentPrinted: (() -> Unit)?,
    ) {
        viewModelScope.launch {
            uiState.value.isLoading = true
            val document = uiState.value.toDocument(onStatusUpdate)
                ?: return@launch

            runCatching {
                documentProcessor.processPrint(
                    documentName = getString(DOCUMENT_NAME)
                            + " № " + uiState.value.number,
                    documentResourceFile = getString(DOCUMENT_RESOURCE),
                    document = document
                )
                onStatusUpdate.invoke(getString(Res.string.form_printed))
                onDocumentPrinted?.invoke()
            }.onFailure { e ->
                onStatusUpdate.invoke(getString(Res.string.form_error_failed_printing))
                e.printStackTrace()
            }
            uiState.value.isLoading = false
        }
    }
}
