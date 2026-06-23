package tech.ilug.documentmanager.model

interface Document {
    fun replacements(): Map<String, String>
}