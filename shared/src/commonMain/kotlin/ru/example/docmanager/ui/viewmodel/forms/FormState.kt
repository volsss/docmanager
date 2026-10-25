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

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.datetime.LocalDate
import ru.example.docmanager.data.document.Document
import ru.example.docmanager.data.document.DocumentType

abstract class FormState<D : Document>(
    val type: DocumentType,
    open val today: LocalDate,
) {
    var selectedDocumentId by mutableStateOf<Int?>(null)
    val isEditing: Boolean
        get() = selectedDocumentId != null

    // TODO make Int
    var number by mutableStateOf("1")
    var dischargeDate by mutableStateOf(today)

    abstract val isLoading: Boolean
    abstract fun populateFromDocument(document: D)
    abstract suspend fun toDocument(onError: ((String) -> Unit)?): D?
}