/*
 * Copyright (c) 2026 Andrew Z.
 * docmanager — a scalable document management system.
 */

package ru.example.docmanager.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import ru.example.docmanager.data.document.Document
import ru.example.docmanager.data.repositories.documents.DocumentRepository

class DocumentsViewModel(
    private val repository: DocumentRepository<*>,
) : ViewModel() {
    private val _documents = MutableStateFlow<List<Document>>(emptyList())
    val documents: StateFlow<List<Document>> = _documents

    fun loadDocuments() {
        viewModelScope.launch {
            runCatching {
                repository.getAllDocuments()
            }.onSuccess { docs ->
                _documents.value = docs.sortedBy { it.header.number }
            }
        }
    }
}