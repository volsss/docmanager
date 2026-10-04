package ru.example.docmanager.database.models.documents

interface DocumentBody<T : DocumentBodyItem> {
    val items: List<T>
    fun toMapByColumns(): Map<String, List<String>>
}