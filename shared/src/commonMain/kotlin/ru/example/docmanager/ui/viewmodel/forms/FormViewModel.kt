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

package ru.example.docmanager.ui.viewmodel.forms

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import docmanager.shared.generated.resources.*
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.getString
import ru.example.docmanager.data.document.Document
import ru.example.docmanager.data.repositories.documents.DocumentRepository
import ru.example.docmanager.di.DocumentProcessor
import ru.example.docmanager.di.getDocumentProcessor


abstract class FormViewModel<T : Document, S : FormState<T>>(
    val repository: DocumentRepository<T>,
    val documentName: StringResource,
    val documentResource: StringResource,
) : ViewModel() {
    protected val documentProcessor: DocumentProcessor = getDocumentProcessor()

    abstract val uiState: StateFlow<S>

    abstract fun onInit(
        onStatusUpdate: (String) -> Unit,
        document: Document? = null,
    )

    abstract fun setLoading(loading: Boolean)

    fun onSaveDocument(
        onStatusUpdate: (String) -> Unit,
        onDocumentUpdated: (() -> Unit)? = null,
        onDocumentSaved: ((T) -> Unit)? = null,
    ) {
        viewModelScope.launch {
            setLoading(true)
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
            setLoading(false)
        }
    }

    fun onSaveAsNewDocument(
        onStatusUpdate: (String) -> Unit,
        onDocumentSaved: ((T) -> Unit)? = null,
    ) {
        viewModelScope.launch {
            setLoading(true)
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
            setLoading(false)
        }
    }

    fun onDeleteDocument(
        onStatusUpdate: (String) -> Unit,
        onDocumentDeleted: (() -> Unit)? = null,
    ) {
        viewModelScope.launch {
            setLoading(true)
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
            setLoading(false)
        }
    }

    fun onExportToFile(
        onStatusUpdate: (String) -> Unit,
        onDocumentExported: (() -> Unit)? = null,
    ) {
        viewModelScope.launch {
            setLoading(true)
            val document = uiState.value.toDocument(onStatusUpdate)
                ?: return@launch

            runCatching {
                documentProcessor.processSave(
                    documentName = getString(documentName)
                            + " № " + uiState.value.number,
                    documentResourceFile = getString(documentResource),
                    document = document
                )
                onStatusUpdate.invoke(getString(Res.string.form_exported))
                onDocumentExported?.invoke()
            }.onFailure { e ->
                onStatusUpdate.invoke(getString(Res.string.form_error_failed_exporting))
                e.printStackTrace()
            }
            setLoading(false)
        }
    }

    fun onPrintDocument(
        onStatusUpdate: (String) -> Unit,
        onDocumentPrinted: (() -> Unit)? = null,
    ) {
        viewModelScope.launch {
            setLoading(true)
            val document = uiState.value.toDocument(onStatusUpdate)
                ?: return@launch

            runCatching {
                documentProcessor.processPrint(
                    documentName = getString(documentName)
                            + " № " + uiState.value.number,
                    documentResourceFile = getString(documentResource),
                    document = document
                )
                onStatusUpdate.invoke(getString(Res.string.form_printed))
                onDocumentPrinted?.invoke()
            }.onFailure { e ->
                onStatusUpdate.invoke(getString(Res.string.form_error_failed_printing))
                e.printStackTrace()
            }
            setLoading(false)
        }
    }
}