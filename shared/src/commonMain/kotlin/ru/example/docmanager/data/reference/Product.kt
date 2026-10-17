/*
 * Copyright (c) 2026 Andrew Z.
 * docmanager — a scalable document management system.
 */

package ru.example.docmanager.data.reference

import ru.example.docmanager.data.document.header.HeaderFieldType

data class Product(
    override var id: Int = -1,
    val name: String = ""
) : Reference(id, ReferenceType.PRODUCT) {
    override fun toMap() = mapOf(
        HeaderFieldType.PRODUCT_NAME to name,
    )

    override fun copyWithFields(fields: Map<HeaderFieldType, String>) = copy(
        name = (fields[HeaderFieldType.PRODUCT_NAME] ?: name)
    )
}