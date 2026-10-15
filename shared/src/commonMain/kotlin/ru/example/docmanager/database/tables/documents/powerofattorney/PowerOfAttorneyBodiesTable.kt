/*
 * Copyright (c) 2026 Andrew Z.
 * docmanager — a scalable document management system.
 */

package ru.example.docmanager.database.tables.documents.powerofattorney

import org.jetbrains.exposed.v1.core.ReferenceOption
import org.jetbrains.exposed.v1.core.dao.id.IntIdTable
import ru.example.docmanager.database.tables.references.ProductsTable

object PowerOfAttorneyBodiesTable : IntIdTable("powerOfAttorneyBodies") {
    val count = varchar("count", 64).default("одна")
    val unit = varchar("unit", 256)
    val headerId = integer("headerId")
        .references(PowerOfAttorneyHeadersTable.id, onDelete = ReferenceOption.CASCADE)
    val productId = integer("productId")
        .references(ProductsTable.id, onDelete = ReferenceOption.CASCADE)
}