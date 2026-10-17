/*
 * Copyright (c) 2026 Andrew Z.
 * docmanager — a scalable document management system.
 */

package ru.example.docmanager.data.database.tables.documents.powerofattorney

import org.jetbrains.exposed.v1.core.ReferenceOption
import org.jetbrains.exposed.v1.core.dao.id.IntIdTable
import org.jetbrains.exposed.v1.javatime.date
import ru.example.docmanager.data.database.tables.references.IndividualsTable
import ru.example.docmanager.data.database.tables.references.OrganizationsTable
import ru.example.docmanager.data.database.tables.references.SuppliersTable

object PowerOfAttorneyHeadersTable : IntIdTable("powerOfAttorneyHeaders") {
    val organizationId = integer("organizationId")
        .references(OrganizationsTable.id, onDelete = ReferenceOption.CASCADE)
    val number = integer("number")
    val dischargeDate = date("dischargeDate")
    val endDate = date("endDate")
    val individualId = integer("individualId")
        .references(IndividualsTable.id, onDelete = ReferenceOption.CASCADE)
    val supplierId = integer("supplierId")
        .references(SuppliersTable.id, onDelete = ReferenceOption.CASCADE)
    val supplierAgreement = varchar("supplierAgreement", 256)
}