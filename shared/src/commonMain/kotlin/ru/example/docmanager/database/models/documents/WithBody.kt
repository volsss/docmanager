package ru.example.docmanager.database.models.documents

interface WithBody <T: DocumentBodyItem> {
    val body: DocumentBody<T>
}
