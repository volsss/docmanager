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