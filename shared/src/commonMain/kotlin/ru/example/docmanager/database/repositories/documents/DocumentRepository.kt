package ru.example.docmanager.database.repositories.documents

import ru.example.docmanager.database.models.documents.DocumentModel

interface DocumentRepository<T: DocumentModel> {
    suspend fun createDocument(document: T)
    suspend fun updateDocument(document: T)
    suspend fun deleteDocument(id: Int)
    suspend fun getDocument(id: Int): T
    suspend fun getAllDocuments(): List<T>
}