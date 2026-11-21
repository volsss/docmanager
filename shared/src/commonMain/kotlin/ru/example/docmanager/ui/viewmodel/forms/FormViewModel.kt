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
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.getString
import ru.example.docmanager.data.repositories.documents.DocumentRepository
import ru.example.docmanager.domain.document.Document
import ru.example.docmanager.domain.document.DocumentProcessor


abstract class FormViewModel<T : Document, S : FormState<T>>(
    val repository: DocumentRepository<T>,
    val documentName: StringResource,
    val documentResource: StringResource,
    val documentProcessor: DocumentProcessor,
) : ViewModel() {

    abstract val uiState: StateFlow<S>

    protected val _events = MutableSharedFlow<FormEvent>()
    val events: Flow<FormEvent> = _events.asSharedFlow()

    abstract fun onInit(
        document: Document? = null,
    )

    abstract fun setLoading(loading: Boolean)

    fun onSaveDocument() {
        viewModelScope.launch {
            setLoading(true)
            val document = uiState.value.toDocument(null)
                ?: run {
                    setLoading(false)
                    return@launch
                }

            runCatching {
                if (uiState.value.isEditing) {
                    repository.updateDocument(document)
                    _events.emit(FormEvent.DocumentUpdated)
                } else {
                    _events.emit(FormEvent.DocumentSaved(repository.createDocument(document)))
                }
                _events.emit(FormEvent.Status(getString(Res.string.form_saved)))
            }.onFailure { e ->
                if (e is CancellationException) {
                    ensureActive()
                }
                _events.emit(FormEvent.Error(getString(Res.string.form_error_failed_saving)))
            }
            setLoading(false)
        }
    }

    fun onSaveAsNewDocument() {
        viewModelScope.launch {
            setLoading(true)
            val document = uiState.value.toDocument(null)
                ?: run {
                    setLoading(false)
                    return@launch
                }

            runCatching {
                val newDocument = repository.createDocument(document)
                _events.emit(FormEvent.Status(getString(Res.string.form_saved_as_new)))
                _events.emit(FormEvent.DocumentSaved(newDocument))
            }.onFailure { e ->
                if (e is CancellationException) {
                    ensureActive()
                }
                _events.emit(FormEvent.Error(getString(Res.string.form_error_failed_saving)))
            }
            setLoading(false)
        }
    }

    fun onDeleteDocument() {
        viewModelScope.launch {
            setLoading(true)
            val docId = uiState.value.documentId ?: run {
                setLoading(false)
                return@launch
            }
            runCatching {
                repository.deleteDocument(docId)
                _events.emit(FormEvent.Status(getString(Res.string.form_deleted)))
                _events.emit(FormEvent.DocumentDeleted)
            }.onFailure { e ->
                if (e is CancellationException) {
                    ensureActive()
                }
                _events.emit(FormEvent.Error(getString(Res.string.form_error_failed_deleting)))
            }
            setLoading(false)
        }
    }

    fun onExportToFile() {
        viewModelScope.launch {
            setLoading(true)
            val document = uiState.value.toDocument(null)
                ?: run {
                    setLoading(false)
                    return@launch
                }

            runCatching {
                documentProcessor.processSave(
                    documentName = getString(documentName)
                            + getString(Res.string.form_document_number_separator) + uiState.value.number,
                    documentResourceFile = getString(documentResource),
                    document = document,
                    onFailure = { msg ->
                        viewModelScope.launch {
                            _events.emit(FormEvent.Error(msg))
                        }
                    },
                    onSuccess = { s ->
                        viewModelScope.launch {
                            _events.emit(FormEvent.Status(s))
                            _events.emit(FormEvent.DocumentExported)
                        }
                    }
                )
            }.onFailure { e ->
                if (e is CancellationException) {
                    ensureActive()
                }
                _events.emit(FormEvent.Error(getString(Res.string.form_error_failed_exporting)))
            }
            setLoading(false)
        }
    }

    fun onPrintDocument() {
        viewModelScope.launch {
            setLoading(true)
            val document = uiState.value.toDocument(null)
                ?: run {
                    setLoading(false)
                    return@launch
                }

            runCatching {
                documentProcessor.processPrint(
                    documentName = getString(documentName)
                            + getString(Res.string.form_document_number_separator) + uiState.value.number,
                    documentResourceFile = getString(documentResource),
                    document = document,
                    onFailure = { msg ->
                        viewModelScope.launch {
                            _events.emit(FormEvent.Error(msg))
                        }
                    },
                    onSuccess = { s ->
                        viewModelScope.launch {
                            _events.emit(FormEvent.Status(s))
                            _events.emit(FormEvent.DocumentPrinted)
                        }
                    }
                )
            }.onFailure { e ->
                if (e is CancellationException) {
                    ensureActive()
                }
                _events.emit(FormEvent.Error(getString(Res.string.form_error_failed_printing)))
            }
            setLoading(false)
        }
    }
}