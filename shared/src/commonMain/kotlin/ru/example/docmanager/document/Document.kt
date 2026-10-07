package ru.example.docmanager.document

import ru.example.docmanager.document.header.DocumentHeader

interface Document {
    val header: DocumentHeader
}