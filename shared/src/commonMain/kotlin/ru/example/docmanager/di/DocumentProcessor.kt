package ru.example.docmanager.di

import ru.example.docmanager.document.Document

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