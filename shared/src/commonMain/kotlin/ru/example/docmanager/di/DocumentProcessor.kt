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

package ru.example.docmanager.di

import ru.example.docmanager.data.document.Document

interface DocumentProcessor {
    suspend fun processSave(
        documentName: String,
        documentResourceFile: String,
        document: Document
    )

    suspend fun processPrint(
        documentName: String,
        documentResourceFile: String,
        document: Document
    )
}