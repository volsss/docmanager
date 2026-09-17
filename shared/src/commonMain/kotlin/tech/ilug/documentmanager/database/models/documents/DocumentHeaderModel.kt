package tech.ilug.documentmanager.database.models.documents

interface DocumentHeaderModel {
    fun replacements(): Map<String, String>
}