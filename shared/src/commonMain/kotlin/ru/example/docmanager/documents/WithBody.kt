package ru.example.docmanager.documents

interface WithBody <T: DocumentBodyItem> {
    val body: DocumentBody<T>
}
