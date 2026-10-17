/*
 * Copyright (c) 2026 Andrew Z.
 * docmanager — a scalable document management system.
 */

package ru.example.docmanager.data.document

interface Document {
    val type: DocumentType
    val header: ru.example.docmanager.data.document.header.DocumentHeader
}