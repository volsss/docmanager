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

package ru.example.docmanager.ui.viewmodel.reference

import ru.example.docmanager.data.document.header.HeaderFieldType
import ru.example.docmanager.data.reference.Reference
import ru.example.docmanager.data.reference.ReferenceType

data class ReferenceUiState(
    val referenceType: ReferenceType = ReferenceType.INDIVIDUAL,
    var itemsByType: Map<ReferenceType, List<Reference>> = emptyMap(),
    var fieldValues: Map<ReferenceType, Map<Int, Map<HeaderFieldType, String>>> = emptyMap(),
    val isLoading: Boolean = false,
    val statusMessage: String? = null
) {
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

    fun syncFieldValues() {
        TODO("Not yet implemented")
    }

    fun getFieldValues(type: ReferenceType, item: Reference): Map<HeaderFieldType, String> {
        val edits = fieldValues[type]?.get(item.id)
        return edits ?: item.toMap()
    }

    fun getCurrentFieldValues(item: Reference): Map<HeaderFieldType, String> {
        return getFieldValues(referenceType, item)
    }

    fun buildUpdatedItems(type: ReferenceType = referenceType): List<Reference> {
        val items = itemsByType[type].orEmpty()
        val typeFieldEdits = fieldValues[type].orEmpty()
        return items.map { item ->
            val fields = typeFieldEdits[item.id] ?: item.toMap()
            item.copyWithFields(fields)
        }
    }
}
