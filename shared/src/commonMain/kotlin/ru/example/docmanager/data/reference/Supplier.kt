/*
 * Copyright (c) 2026 Andrew Z.
 * docmanager — a scalable document management system.
 */

package ru.example.docmanager.data.reference

import ru.example.docmanager.data.document.header.HeaderFieldType

data class Supplier(
    override var id: Int = -1,
    val name: String = ""
) : Reference(id, ReferenceType.SUPPLIER) {
    override fun toMap() = mapOf(
        HeaderFieldType.SUPPLIER_NAME to name
    )

    override fun copyWithFields(fields: Map<HeaderFieldType, String>) = copy(
        name = (fields[HeaderFieldType.SUPPLIER_NAME] ?: name)
    )
}