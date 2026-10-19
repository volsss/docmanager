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

package ru.example.docmanager.data.repositories.documents

import ru.example.docmanager.data.document.Document

interface DocumentRepository<T : Document> : BaseDocumentRepository {
    suspend fun createDocument(document: T)
    suspend fun updateDocument(document: T)
    suspend fun getDocument(id: Int): T
    suspend fun getAllDocuments(): List<T>
}