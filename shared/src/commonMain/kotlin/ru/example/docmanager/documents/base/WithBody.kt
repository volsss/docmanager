package ru.example.docmanager.documents.base

interface WithBody <T: DocumentBodyItem> {
    val body: DocumentBody<T>
}
