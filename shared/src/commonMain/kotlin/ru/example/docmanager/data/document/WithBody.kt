/*
 * Copyright (c) 2026 Andrew Z.
 * docmanager — a scalable document management system.
 */

package ru.example.docmanager.data.document

import ru.example.docmanager.data.document.body.DocumentBody
import ru.example.docmanager.data.document.body.DocumentBodyItem

interface WithBody<T : DocumentBodyItem> {
    val body: DocumentBody<T>
}
