package ru.example.docmanager.database.models.documents

interface DocumentHeaderModel {
    fun replacements(): Map<String, String>
}