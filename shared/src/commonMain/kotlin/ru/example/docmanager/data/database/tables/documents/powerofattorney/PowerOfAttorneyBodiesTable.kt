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

package ru.example.docmanager.data.database.tables.documents.powerofattorney

import org.jetbrains.exposed.v1.core.ReferenceOption
import org.jetbrains.exposed.v1.core.dao.id.IntIdTable
import ru.example.docmanager.data.database.tables.references.ProductsTable

object PowerOfAttorneyBodiesTable : IntIdTable("powerOfAttorneyBodies") {
    val count = varchar("count", 64).default("одна")
    val unit = varchar("unit", 256)
    val headerId = integer("headerId")
        .references(PowerOfAttorneyHeadersTable.id, onDelete = ReferenceOption.CASCADE)
    val productId = integer("productId")
        .references(ProductsTable.id, onDelete = ReferenceOption.CASCADE)
}