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

package ru.example.docmanager.ui.viewmodel.documents

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.example.docmanager.data.document.Document
import ru.example.docmanager.data.document.DocumentType
import ru.example.docmanager.data.repositories.documents.BaseDocumentRepository
import ru.example.docmanager.data.repositories.documents.DocumentRepository
import ru.example.docmanager.ui.router.Route
import kotlin.time.Duration.Companion.milliseconds

class DocumentsViewModel(
    private val repositories: Map<DocumentType, BaseDocumentRepository>,
    private val destination: Route.Documents
) : ViewModel() {
    val documents: StateFlow<List<Document>?>
        field = MutableStateFlow<List<Document>?>(null)

    fun reloadDocumentList() {
        viewModelScope.launch {
            delay(280.milliseconds)

            withContext(Dispatchers.IO) {
                runCatching {
                    (repositories[destination.type] as? DocumentRepository<*>)
                        ?.getAllDocuments()
                }.onSuccess { docs ->
                    val sortedDocs = docs?.sortedBy { it.header.number } ?: emptyList()

                    documents.value = sortedDocs
                }
            }
        }
    }
}