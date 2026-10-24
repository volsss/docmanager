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

abstract class FormState<D : Document, B : BodyItem>(
    val type: DocumentType,
    open val today: LocalDate,
) {
    var selectedDocumentId by mutableStateOf<Int?>(null)

    val isEditing: Boolean
        get() = selectedDocumentId != null

    var statusMessage by mutableStateOf<String?>(null)

    var number by mutableStateOf("1")
    var dischargeDate by mutableStateOf(today)

    var bodyItems: List<B> by mutableStateOf(emptyList())

    fun updateBodyItem(index: Int, item: B) {
        bodyItems = bodyItems.toMutableList().also {
            if (index in it.indices) it[index] = item
        }
    }

    fun removeBodyItem(index: Int) {
        bodyItems = if (bodyItems.size > 1) {
            bodyItems.filterIndexed { i, _ -> i != index }
        } else {
            listOf()
        }
    }

    abstract fun addBodyItem()
    abstract fun resetForm()
    abstract fun populateFromDocument(document: D)

    abstract fun toDocument(): D?
}