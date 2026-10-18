/*
 * Copyright (c) 2026 Andrew Z.
 * docmanager — a scalable document management system.
 */

package ru.example.docmanager.ui.screens.document.forms

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.datetime.LocalDate
import ru.example.docmanager.data.document.Document
import ru.example.docmanager.ui.toRussianString

abstract class FormState<D : Document, B : BodyItem>(
    open val today: LocalDate,
) {
    var existingDocuments by mutableStateOf<List<D>>(emptyList())
    var selectedDocumentId by mutableStateOf<Int?>(null)
    var numberDropdownExpanded by mutableStateOf(false)

    val isEditing: Boolean
        get() = selectedDocumentId != null

    var statusMessage by mutableStateOf<String?>(null)

    var number by mutableStateOf("1")
    var dischargeDate by mutableStateOf(today.toRussianString())

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