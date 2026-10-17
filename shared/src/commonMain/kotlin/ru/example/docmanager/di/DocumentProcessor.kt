/*
 * Copyright (c) 2026 Andrew Z.
 * docmanager — a scalable document management system.
 */

package ru.example.docmanager.di

interface DocumentProcessor {
    suspend fun processSave(
        documentName: String,
        documentResourceFile: String,
        document: ru.example.docmanager.data.document.Document
    )

    suspend fun processPrint(
        documentName: String,
        documentResourceFile: String,
        document: ru.example.docmanager.data.document.Document
    )
}