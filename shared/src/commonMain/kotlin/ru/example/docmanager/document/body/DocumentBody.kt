package ru.example.docmanager.document.body

interface DocumentBody<T : DocumentBodyItem> {
    val items: List<T>
    fun toMapByColumns(): Map<BodyFieldType, List<String>>
}