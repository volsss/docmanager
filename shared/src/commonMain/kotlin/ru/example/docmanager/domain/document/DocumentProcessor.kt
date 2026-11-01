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

package ru.example.docmanager.domain.document

interface DocumentProcessor {
    suspend fun processSave(
        documentName: String,
        documentResourceFile: String,
        document: Document,
        onFailure: (String) -> Unit,
        onSuccess: (String) -> Unit
    )

    suspend fun processPrint(
        documentName: String,
        documentResourceFile: String,
        document: Document,
        onFailure: (String) -> Unit,
        onSuccess: (String) -> Unit
    )
}