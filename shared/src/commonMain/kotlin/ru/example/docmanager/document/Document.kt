/*
 * Copyright (c) 2026 Andrew Z.
 * docmanager — a scalable document management system.
 */

package ru.example.docmanager.document

import ru.example.docmanager.document.header.DocumentHeader

interface Document {
    val header: DocumentHeader
}