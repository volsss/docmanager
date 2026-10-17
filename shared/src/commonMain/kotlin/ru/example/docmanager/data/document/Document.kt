/*
 * Copyright (c) 2026 Andrew Z.
 * docmanager — a scalable document management system.
 */

package ru.example.docmanager.data.document

import ru.example.docmanager.data.document.header.DocumentHeader

interface Document {
    val type: DocumentType
    val header: DocumentHeader
}