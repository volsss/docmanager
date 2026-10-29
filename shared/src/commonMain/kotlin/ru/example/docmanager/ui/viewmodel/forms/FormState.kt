/*
 * Copyright (c) 2026 Andrew Z.
 * docmanager — a scalable document management system.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License v3.0.
 *
 * Distributed WITHOUT ANY WARRANTY. See the GNU General Public License
 * for more details: <https://gnu.org>
 */

package ru.example.docmanager.ui.viewmodel.forms

import kotlinx.datetime.LocalDate
import ru.example.docmanager.data.document.Document
import ru.example.docmanager.data.document.DocumentType

abstract class FormState<D : Document>(
    val type: DocumentType
) {
    abstract val documentId: Int?
    abstract val number: Int
    abstract val dischargeDate: LocalDate

    abstract val isLoading: Boolean
    val isEditing: Boolean
        get() = documentId != null

    abstract suspend fun toDocument(onError: ((String) -> Unit)?): D?
}