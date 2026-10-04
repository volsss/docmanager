package ru.example.docmanager.database.models.documents

import ru.example.docmanager.database.models.FieldType

interface Document {
    val header: DocumentHeader
    fun toMap(): Map<FieldType, String>
}