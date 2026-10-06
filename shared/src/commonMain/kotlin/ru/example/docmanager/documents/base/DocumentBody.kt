package ru.example.docmanager.documents.base

interface DocumentBody<T : DocumentBodyItem> {
    val items: List<T>
    fun toMapByColumns(): Map<BodyFieldType, List<String>>
}