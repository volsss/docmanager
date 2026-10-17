/*
 * Copyright (c) 2026 Andrew Z.
 * docmanager — a scalable document management system.
 */

package ru.example.docmanager.data.document

interface WithBody<T : ru.example.docmanager.data.document.body.DocumentBodyItem> {
    val body: ru.example.docmanager.data.document.body.DocumentBody<T>
}
