/*
 * Copyright (c) 2026 Andrew Z.
 * docmanager — a scalable document management system.
 */

package ru.example.docmanager.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import ru.example.docmanager.database.repositories.documents.DocumentRepository
import ru.example.docmanager.document.Document

class DocumentsViewModel (
    private val repository: DocumentRepository<*>,
) : ViewModel() {
    val documents: StateFlow<List<Document>>
        field = MutableStateFlow<List<Document>>(emptyList())

    init {
        viewModelScope.launch {
            documents.value = repository.getAllDocuments()
        }
    }
}