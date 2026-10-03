package ru.example.docmanager.database.models.documents

interface Document {
    val header: DocumentHeader
    fun toMap(): Map<String, String>
}