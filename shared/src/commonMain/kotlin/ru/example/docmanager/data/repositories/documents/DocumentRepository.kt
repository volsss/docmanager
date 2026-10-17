/*
 * Copyright (c) 2026 Andrew Z.
 * docmanager — a scalable document management system.
 */

package ru.example.docmanager.data.repositories.documents

import ru.example.docmanager.data.document.Document

interface DocumentRepository<T : Document> : BaseDocumentRepository {
    suspend fun createDocument(document: T)
    suspend fun updateDocument(document: T)
    suspend fun getDocument(id: Int): T
    suspend fun getAllDocuments(): List<T>
}