/*
 * Copyright (c) 2026 Andrew Z.
 * docmanager — a scalable document management system.
 */

package ru.example.docmanager.document

import ru.example.docmanager.document.body.DocumentBody
import ru.example.docmanager.document.body.DocumentBodyItem

interface WithBody <T: DocumentBodyItem> {
    val body: DocumentBody<T>
}
