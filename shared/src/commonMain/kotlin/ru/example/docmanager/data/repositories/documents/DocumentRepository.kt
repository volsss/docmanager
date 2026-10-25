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

    /**
     * Creates and saves a new document in the repository.
     *
     * @param document the document to be created and saved
     * @return the newly created and saved document
     */
    suspend fun createDocument(document: T): T

    /**
     * Updates an existing document in the repository.
     *
     * @param document the document to be updated
     */
    suspend fun updateDocument(document: T)

    /**
     * Retrieves a document from the repository by its ID.
     *
     * @param id the ID of the document to retrieve
     * @return the retrieved document
     */
    suspend fun getDocument(id: Int): T

    /**
     * Retrieves all documents from the repository.
     *
     * @return a list of all documents in the repository
     */
    suspend fun getAllDocuments(): List<T>
}