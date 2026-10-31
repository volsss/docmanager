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

package ru.example.docmanager.data.reference

import ru.example.docmanager.domain.document.header.HeaderFieldType
import ru.example.docmanager.domain.reference.Reference
import ru.example.docmanager.domain.reference.ReferenceType

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