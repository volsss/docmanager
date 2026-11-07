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

package ru.example.docmanager.ui.viewmodel.start

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import org.jetbrains.exposed.v1.jdbc.transactions.TransactionManager
import ru.example.docmanager.data.database.DatabaseManager
import ru.example.docmanager.data.repositories.MetadataRepository
import ru.example.docmanager.data.repositories.documents.BaseDocumentRepository
import ru.example.docmanager.data.repositories.documents.DocumentRepository
import ru.example.docmanager.data.repositories.references.BaseReferenceRepository
import ru.example.docmanager.data.repositories.references.ReferenceRepository
import ru.example.docmanager.domain.document.DocumentType
import ru.example.docmanager.domain.reference.ReferenceType

class StartViewModel(
    private val metadataRepository: MetadataRepository,
    private val databaseManager: DatabaseManager,
    private val documentRepositories: Map<DocumentType, BaseDocumentRepository>,
    private val referenceRepositories: Map<ReferenceType, BaseReferenceRepository>
) : ViewModel() {

    val uiState: StateFlow<StartUiState>
        field = MutableStateFlow(StartUiState())

    init {
        loadStats()
    }

    fun refresh() {
        viewModelScope.launch {
            uiState.update { it.copy(isRefreshing = true) }
            loadStats()
        }
    }

    private fun loadStats() {
        viewModelScope.launch {
            try {
                val metadata = withContext(Dispatchers.IO) {
                    metadataRepository.getMetadata()
                }
                val isConnected = databaseManager.isInitialized()

                val db = TransactionManager.defaultDatabase
                val (documentTypesCount, documentsCount) = if (isConnected && db != null) {
                    val typesCount = documentRepositories.size
                    val docsCount = withContext(Dispatchers.IO) {
                        documentRepositories.values.sumOf { repo ->
                            when (repo) {
                                is DocumentRepository<*> -> repo.getAllDocuments().size
                                else -> 0
                            }
                        }
                    }
                    typesCount to docsCount
                } else {
                    0 to 0
                }

                val (referenceTypesCount, referencesCount) = if (isConnected && db != null) {
                    val typesCount = referenceRepositories.size
                    val refsCount = withContext(Dispatchers.IO) {
                        referenceRepositories.values.sumOf { repo ->
                            when (repo) {
                                is ReferenceRepository<*> -> repo.getItems().size
                                else -> 0
                            }
                        }
                    }
                    typesCount to refsCount
                } else {
                    0 to 0
                }

                uiState.value = StartUiState(
                    projectName = metadata.name,
                    version = metadata.version,
                    author = metadata.author,
                    creationDate = metadata.creationDate,
                    documentTypesCount = documentTypesCount,
                    documentsCount = documentsCount,
                    referenceTypesCount = referenceTypesCount,
                    referencesCount = referencesCount,
                    isLoading = false,
                    isRefreshing = false
                )
            } catch (e: Exception) {
                if (e is CancellationException) {
                    ensureActive()
                }
                uiState.update {
                    it.copy(
                        isLoading = false,
                        isRefreshing = false,
                        error = e.localizedMessage ?: "Failed to load stats"
                    )
                }
            }
        }
    }
}
