package ru.example.docmanager.document

interface WithBody <T: DocumentBodyItem> {
    val body: DocumentBody<T>
}
