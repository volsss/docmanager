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
import kotlinx.coroutines.flow.StateFlow
import ru.example.docmanager.data.document.Document
import ru.example.docmanager.di.DocumentProcessor
import ru.example.docmanager.di.getDocumentProcessor

abstract class FormViewModel<T : Document, S : FormState<T>> : ViewModel() {
    protected val documentProcessor: DocumentProcessor = getDocumentProcessor()

    abstract val uiState: StateFlow<S>

    abstract fun onInit(
        onStatusUpdate: (String) -> Unit,
        document: Document? = null,
    )

    abstract fun onSaveDocument(
        onStatusUpdate: (String) -> Unit,
        onDocumentUpdated: (() -> Unit)? = null,
        onDocumentSaved: ((T) -> Unit)? = null,
    )

    abstract fun onSaveAsNewDocument(
        onStatusUpdate: (String) -> Unit,
        onDocumentSaved: ((T) -> Unit)? = null,
    )

    abstract fun onDeleteDocument(
        onStatusUpdate: (String) -> Unit,
        onDocumentDeleted: (() -> Unit)? = null,
    )

    abstract fun onExportToFile(
        onStatusUpdate: (String) -> Unit,
        onDocumentExported: (() -> Unit)? = null,
    )

    abstract fun onPrintDocument(
        onStatusUpdate: (String) -> Unit,
        onDocumentPrinted: (() -> Unit)? = null,
    )
}