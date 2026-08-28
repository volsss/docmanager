package tech.ilug.documentmanager.repository.documents

import tech.ilug.documentmanager.model.Document

interface DocumentRepository<T: Document> {
    suspend fun createDocument(document: T)
    suspend fun updateDocument(document: T)
    suspend fun deleteDocument(id: Int)
    suspend fun getDocument(id: Int): T
    suspend fun getAllDocuments(): List<T>
}