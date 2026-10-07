/*
 * Copyright (c) 2026 Andrew Z.
 * docmanager — a scalable document management system.
 */

package ru.example.docmanager.database.repositories.documents

import ru.example.docmanager.document.Document

interface DocumentRepository<T: Document> {
    suspend fun createDocument(document: T)
    suspend fun updateDocument(document: T)
    suspend fun deleteDocument(id: Int)
    suspend fun getDocument(id: Int): T
    suspend fun getAllDocuments(): List<T>
}