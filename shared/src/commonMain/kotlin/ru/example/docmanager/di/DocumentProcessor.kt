package ru.example.docmanager.di

import ru.example.docmanager.documents.Document

interface DocumentProcessor {
    fun processSave(
        documentName: String,
        documentResourceFile: String,
        document: Document
    )

    fun processPrint(
        documentName: String,
        documentResourceFile: String,
        document: Document
    )
}