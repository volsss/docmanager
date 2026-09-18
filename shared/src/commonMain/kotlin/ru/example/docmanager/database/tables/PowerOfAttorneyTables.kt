package ru.example.docmanager.database.tables

import org.jetbrains.exposed.v1.core.ReferenceOption
import org.jetbrains.exposed.v1.core.dao.id.IntIdTable
import org.jetbrains.exposed.v1.javatime.date

object PowerOfAttorneyTables {
    object HeadersTable : IntIdTable("powerOfAttorneyHeaders") {
        val organizationId = integer("organizationId").references(OrganizationsTable.id, onDelete = ReferenceOption.CASCADE)
        val number = integer("number")
        val dischargeDate = date("dischargeDate")
        val endDate = date("endDate")
        val individualId = integer("individualId").references(IndividualsTable.id, onDelete = ReferenceOption.CASCADE)
        val supplierId = integer("supplierId").references(SuppliersTable.id, onDelete = ReferenceOption.CASCADE)
        val supplierAgreement = varchar("supplierAgreement", 256)
    }

    object OrganizationsTable : IntIdTable("powerOfAttorneyOrganizations") {
        val name = varchar("name", 256)
        val consumer = varchar("consumer", 256)
        val payer = varchar("payer", 256)
        val account = varchar("account", 256)
    }

    object SuppliersTable : IntIdTable("powerOfAttorneySuppliers") {
        val name = varchar("name", 256)
    }

    object IndividualsTable : IntIdTable("powerOfAttorneyIndividuals") {
        val job = varchar("job", 128)
        val name = varchar("name", 128)
        val series = varchar("series", 16)
        val number = varchar("number", 16)
        val issued = varchar("issued", 128)
        val date = date("date")
    }

    object ProductsTable : IntIdTable("powerOfAttorneyProducts") {
        val name = varchar("name", 256)
    }

    object BodiesTable : IntIdTable("powerOfAttorneyBodies") {
        val count = varchar("count", 64).default("одна")
        val unit = varchar("unit", 256)
        val headerId = integer("headerId").references(HeadersTable.id, onDelete = ReferenceOption.CASCADE)
        val productId = integer("productId").references(ProductsTable.id, onDelete = ReferenceOption.CASCADE)
    }
}
