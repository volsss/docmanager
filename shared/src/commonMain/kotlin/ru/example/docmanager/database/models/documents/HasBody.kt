package ru.example.docmanager.database.models.documents

interface HasBody <B: DocumentBodyItem, T : DocumentBody<B>> {
    val body: T
}