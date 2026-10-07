/*
 * Copyright (c) 2026 Andrew Z.
 * docmanager — a scalable document management system.
 */

package ru.example.docmanager.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import ru.example.docmanager.reference.Reference
import ru.example.docmanager.document.header.HeaderFieldType
import ru.example.docmanager.reference.ReferenceType

class ReferenceState (
    referenceType: ReferenceType = ReferenceType.INDIVIDUAL
) {
    var referenceType by mutableStateOf(referenceType)
    var itemsByType by mutableStateOf<Map<ReferenceType, List<Reference>>>(emptyMap())
    var fieldValues by mutableStateOf<Map<Int, Map<HeaderFieldType, String>>>(emptyMap())
    var statusMessage by mutableStateOf<String?>(null)

    val currentItems: List<Reference>
        get() = itemsByType[referenceType].orEmpty()

    fun setItems(referenceType: ReferenceType, items: List<Reference>) {
        itemsByType = itemsByType + (referenceType to items)
        if (this.referenceType == referenceType) {
            syncFieldValues()
        }
    }

    fun setAllItems(items: Map<ReferenceType, List<Reference>>) {
        itemsByType = items
        syncFieldValues()
    }

    fun updateField(itemId: Int, headerFieldType: HeaderFieldType, value: String) {
        val currentFields = fieldValues[itemId].orEmpty()
        fieldValues = fieldValues + (itemId to (currentFields + (headerFieldType to value)))
    }

    fun addItem(referenceType: ReferenceType, item: Reference) {
        val updatedItems = itemsByType[referenceType].orEmpty() + item
        itemsByType = itemsByType + (referenceType to updatedItems)
        fieldValues = fieldValues + (item.id to item.toMap())
    }

    fun removeItem(referenceType: ReferenceType, item: Reference) {
        val updatedItems = itemsByType[referenceType].orEmpty().filterNot { it.id == item.id }
        itemsByType = itemsByType + (referenceType to updatedItems)
        fieldValues = fieldValues - item.id
    }

    fun replaceItem(referenceType: ReferenceType, item: Reference) {
        val updatedItems = itemsByType[referenceType].orEmpty().map { current ->
            if (current.id == item.id) item else current
        }
        itemsByType = itemsByType + (referenceType to updatedItems)
        fieldValues = fieldValues + (item.id to item.toMap())
    }

    fun buildUpdatedItems(): List<Reference> {
        return currentItems.map { item ->
            val fields = fieldValues[item.id].orEmpty()
            item.copyWithFields(fields)
        }
    }

    fun syncFieldValues() {
        fieldValues = currentItems.associate { item ->
            item.id to item.toMap()
        }
    }
}
