package ru.example.docmanager.document

interface DocumentBody<T : DocumentBodyItem> {
    val items: List<T>
    fun toMapByColumns(): Map<BodyFieldType, List<String>>
}