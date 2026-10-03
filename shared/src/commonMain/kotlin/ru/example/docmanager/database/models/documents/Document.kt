package ru.example.docmanager.database.models.documents

interface Document {
    val header: DocumentHeader
    fun replacements(): Map<String, String>
}