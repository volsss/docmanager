package ru.example.docmanager.database.models.documents

interface DocumentModel {
    fun replacements(): Map<String, String>
}