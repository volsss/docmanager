/*
 * Copyright (c) 2026 Andrew Z.
 * docmanager — a scalable document management system.
 */

package ru.example.docmanager.data.repositories.documents

import ru.example.docmanager.data.document.DocumentType

interface BaseDocumentRepository {
    val type: DocumentType
    suspend fun deleteDocument(id: Int)
}