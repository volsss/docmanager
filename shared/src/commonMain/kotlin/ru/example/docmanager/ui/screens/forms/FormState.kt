package ru.example.docmanager.ui.screens.forms

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.datetime.LocalDate
import ru.example.docmanager.documents.Document
import ru.example.docmanager.references.Reference
import ru.example.docmanager.references.ReferenceType
import ru.example.docmanager.ui.Utils

abstract class FormState<D: Document, B: BodyItem> (
    protected val today: LocalDate,
) {
    var existingDocuments by mutableStateOf<List<D>>(emptyList())
    var selectedDocumentId by mutableStateOf<Int?>(null)
    var numberDropdownExpanded by mutableStateOf(false)

    val isEditing: Boolean
        get() = selectedDocumentId != null

    var statusMessage by mutableStateOf<String?>(null)

    var number by mutableStateOf("1")
    var dischargeDate by mutableStateOf(Utils.DATE_FORMAT.format(today))

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
    abstract fun resetForm(references: Map<ReferenceType, List<Reference>>)
    abstract fun populateFromDocument(document: D, references: Map<ReferenceType, List<Reference>>)
    abstract fun toDocument(): D?
}