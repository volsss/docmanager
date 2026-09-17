package tech.ilug.documentmanager.database.models.documents

interface DocumentModel {
    fun replacements(): Map<String, String>
}