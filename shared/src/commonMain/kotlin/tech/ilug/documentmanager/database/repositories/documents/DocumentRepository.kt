package tech.ilug.documentmanager.database.repositories.documents

import tech.ilug.documentmanager.database.models.documents.DocumentModel

interface DocumentRepository<T: DocumentModel> {
    suspend fun createDocument(document: T)
    suspend fun updateDocument(document: T)
    suspend fun deleteDocument(id: Int)
    suspend fun getDocument(id: Int): T
    suspend fun getAllDocuments(): List<T>
}