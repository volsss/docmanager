/*
 * Copyright (c) 2026 Andrew Z.
 * docmanager — a scalable document management system.
 */

package ru.example.docmanager.data.repositories.documents

interface DocumentRepository<T : ru.example.docmanager.data.document.Document> {
    suspend fun createDocument(document: T)
    suspend fun updateDocument(document: T)
    suspend fun deleteDocument(id: Int)
    suspend fun getDocument(id: Int): T
    suspend fun getAllDocuments(): List<T>
}