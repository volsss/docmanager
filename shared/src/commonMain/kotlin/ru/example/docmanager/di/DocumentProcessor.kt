package ru.example.docmanager.di

import ru.example.docmanager.documents.base.Document

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