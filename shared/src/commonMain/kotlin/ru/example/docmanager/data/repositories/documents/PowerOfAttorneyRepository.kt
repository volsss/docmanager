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

package ru.example.docmanager.data.repositories.documents

import kotlinx.datetime.toJavaLocalDate
import kotlinx.datetime.toKotlinLocalDate
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.core.inList
import org.jetbrains.exposed.v1.jdbc.*
import ru.example.docmanager.data.database.dbQuery
import ru.example.docmanager.data.database.tables.documents.powerofattorney.PowerOfAttorneyBodiesTable
import ru.example.docmanager.data.database.tables.documents.powerofattorney.PowerOfAttorneyHeadersTable
import ru.example.docmanager.data.database.tables.references.IndividualsTable
import ru.example.docmanager.data.database.tables.references.OrganizationsTable
import ru.example.docmanager.data.database.tables.references.ProductsTable
import ru.example.docmanager.data.database.tables.references.SuppliersTable
import ru.example.docmanager.data.document.DocumentType
import ru.example.docmanager.data.document.type.powerofattorney.PowerOfAttorney
import ru.example.docmanager.data.document.type.powerofattorney.PowerOfAttorneyBody
import ru.example.docmanager.data.document.type.powerofattorney.PowerOfAttorneyBodyItem
import ru.example.docmanager.data.document.type.powerofattorney.PowerOfAttorneyHeader
import ru.example.docmanager.data.reference.Individual
import ru.example.docmanager.data.reference.Organization
import ru.example.docmanager.data.reference.Product
import ru.example.docmanager.data.reference.Supplier

class PowerOfAttorneyRepository : DocumentRepository<PowerOfAttorney> {
    override val type = DocumentType.POWER_OF_ATTORNEY

    override suspend fun createDocument(document: PowerOfAttorney): Unit =
        dbQuery {
            val headerId = PowerOfAttorneyHeadersTable.insertAndGetId {
                it[organizationId] = document.header.organization.id
                it[number] = document.header.number
                it[dischargeDate] = document.header.dischargeDate.toJavaLocalDate()
                it[endDate] = document.header.endDate.toJavaLocalDate()
                it[individualId] = document.header.individual.id
                it[supplierId] = document.header.supplier.id
                it[supplierAgreement] = document.header.supplierAgreement
            }.value
            document.body.items.forEach { body ->
                PowerOfAttorneyBodiesTable.insert {
                    it[count] = body.count
                    it[unit] = body.unit
                    it[PowerOfAttorneyBodiesTable.headerId] = headerId
                    it[productId] = body.product.id
                }
            }
        }

    override suspend fun updateDocument(document: PowerOfAttorney): Unit =
        dbQuery {
            PowerOfAttorneyHeadersTable.update({
                PowerOfAttorneyHeadersTable.id eq document.header.id
            }) {
                it[organizationId] = document.header.organization.id
                it[number] = document.header.number
                it[dischargeDate] = document.header.dischargeDate.toJavaLocalDate()
                it[endDate] = document.header.endDate.toJavaLocalDate()
                it[individualId] = document.header.individual.id
                it[supplierId] = document.header.supplier.id
                it[supplierAgreement] = document.header.supplierAgreement
            }
            PowerOfAttorneyBodiesTable.deleteWhere {
                PowerOfAttorneyBodiesTable.headerId eq document.header.id
            }
            document.body.items.forEach { body ->
                PowerOfAttorneyBodiesTable.insert {
                    it[count] = body.count
                    it[unit] = body.unit
                    it[PowerOfAttorneyBodiesTable.headerId] = document.header.id
                    it[productId] = body.product.id
                }
            }
        }

    override suspend fun deleteDocument(id: Int): Unit = dbQuery {
        PowerOfAttorneyBodiesTable.deleteWhere {
            PowerOfAttorneyBodiesTable.headerId eq id
        }
        PowerOfAttorneyHeadersTable.deleteWhere {
            PowerOfAttorneyHeadersTable.id eq id
        }
    }

    override suspend fun getAllDocuments(): List<PowerOfAttorney> = dbQuery {
        val headerRows = PowerOfAttorneyHeadersTable.selectAll().toList()
        if (headerRows.isEmpty()) return@dbQuery emptyList()

        val bodyRows = PowerOfAttorneyBodiesTable.selectAll().toList().groupBy {
            it[PowerOfAttorneyBodiesTable.headerId]
        }

        val orgMap = OrganizationsTable.selectAll().associate {
            it[OrganizationsTable.id].value to Organization(
                id = it[OrganizationsTable.id].value,
                name = it[OrganizationsTable.name],
                consumer = it[OrganizationsTable.consumer],
                payer = it[OrganizationsTable.payer],
                account = it[OrganizationsTable.account]
            )
        }
        val indMap = IndividualsTable.selectAll().associate {
            it[IndividualsTable.id].value to Individual(
                id = it[IndividualsTable.id].value,
                job = it[IndividualsTable.job],
                name = it[IndividualsTable.name],
                series = it[IndividualsTable.series],
                number = it[IndividualsTable.number],
                issued = it[IndividualsTable.issued],
                date = it[IndividualsTable.date].toKotlinLocalDate()
            )
        }
        val supMap = SuppliersTable.selectAll().associate {
            it[SuppliersTable.id].value to Supplier(
                id = it[SuppliersTable.id].value,
                name = it[SuppliersTable.name]
            )
        }
        val prodMap = ProductsTable.selectAll().associate {
            it[ProductsTable.id].value to Product(
                id = it[ProductsTable.id].value,
                name = it[ProductsTable.name]
            )
        }

        headerRows.map { headerRow ->
            val id = headerRow[PowerOfAttorneyHeadersTable.id].value
            val header =
                PowerOfAttorneyHeader(
                    id = id,
                    organization = orgMap[headerRow[PowerOfAttorneyHeadersTable.organizationId]]
                        ?: Organization(),
                    number = headerRow[PowerOfAttorneyHeadersTable.number],
                    dischargeDate = headerRow[PowerOfAttorneyHeadersTable.dischargeDate]
                        .toKotlinLocalDate(),
                    endDate = headerRow[PowerOfAttorneyHeadersTable.endDate]
                        .toKotlinLocalDate(),
                    individual = indMap[headerRow[PowerOfAttorneyHeadersTable.individualId]]
                        ?: Individual(),
                    supplier = supMap[headerRow[PowerOfAttorneyHeadersTable.supplierId]]
                        ?: Supplier(),
                    supplierAgreement = headerRow[PowerOfAttorneyHeadersTable.supplierAgreement]
                )
            val docBodies = bodyRows[id].orEmpty().map { bRow ->
                PowerOfAttorneyBodyItem(
                    id = bRow[PowerOfAttorneyBodiesTable.id].value,
                    count = bRow[PowerOfAttorneyBodiesTable.count],
                    unit = bRow[PowerOfAttorneyBodiesTable.unit],
                    product = prodMap[bRow[PowerOfAttorneyBodiesTable.productId]]
                        ?: Product()
                )
            }
            PowerOfAttorney(
                header = header,
                body = PowerOfAttorneyBody(
                    docBodies
                )
            )
        }
    }

    override suspend fun getDocument(id: Int): PowerOfAttorney =
        dbQuery {
            val headerRow = PowerOfAttorneyHeadersTable.selectAll().where {
                PowerOfAttorneyHeadersTable.id eq id
            }.first()
            val bodyRows = PowerOfAttorneyBodiesTable.selectAll().where {
                PowerOfAttorneyBodiesTable.headerId eq id
            }.toList()

            val orgRow = OrganizationsTable.selectAll().where {
                OrganizationsTable.id eq headerRow[PowerOfAttorneyHeadersTable.organizationId]
            }.firstOrNull()
            val indRow = IndividualsTable.selectAll().where {
                IndividualsTable.id eq headerRow[PowerOfAttorneyHeadersTable.individualId]
            }.firstOrNull()
            val supRow = SuppliersTable.selectAll().where {
                SuppliersTable.id eq headerRow[PowerOfAttorneyHeadersTable.supplierId]
            }.firstOrNull()

            val productIds = bodyRows.map {
                it[PowerOfAttorneyBodiesTable.productId]
            }.distinct()
            val prodMap = if (productIds.isNotEmpty()) {
                ProductsTable.selectAll().where { ProductsTable.id inList productIds }.associate {
                    it[ProductsTable.id].value to Product(
                        id = it[ProductsTable.id].value,
                        name = it[ProductsTable.name]
                    )
                }
            } else emptyMap()

            val organization = orgRow?.let {
                Organization(
                    id = it[OrganizationsTable.id].value,
                    name = it[OrganizationsTable.name],
                    consumer = it[OrganizationsTable.consumer],
                    payer = it[OrganizationsTable.payer],
                    account = it[OrganizationsTable.account]
                )
            } ?: Organization()

            val individual = indRow?.let {
                Individual(
                    id = it[IndividualsTable.id].value,
                    job = it[IndividualsTable.job],
                    name = it[IndividualsTable.name],
                    series = it[IndividualsTable.series],
                    number = it[IndividualsTable.number],
                    issued = it[IndividualsTable.issued],
                    date = it[IndividualsTable.date].toKotlinLocalDate()
                )
            } ?: Individual()

            val supplier = supRow?.let {
                Supplier(
                    id = it[SuppliersTable.id].value,
                    name = it[SuppliersTable.name]
                )
            } ?: Supplier()

            val header =
                PowerOfAttorneyHeader(
                    id = id,
                    organization = organization,
                    number = headerRow[PowerOfAttorneyHeadersTable.number],
                    dischargeDate = headerRow[PowerOfAttorneyHeadersTable.dischargeDate].toKotlinLocalDate(),
                    endDate = headerRow[PowerOfAttorneyHeadersTable.endDate].toKotlinLocalDate(),
                    individual = individual,
                    supplier = supplier,
                    supplierAgreement = headerRow[PowerOfAttorneyHeadersTable.supplierAgreement]
                )

            val bodyItemList = bodyRows.map { row ->
                val prod = prodMap[row[PowerOfAttorneyBodiesTable.productId]]
                    ?: Product()
                PowerOfAttorneyBodyItem(
                    id = row[PowerOfAttorneyBodiesTable.id].value,
                    count = row[PowerOfAttorneyBodiesTable.count],
                    unit = row[PowerOfAttorneyBodiesTable.unit],
                    product = prod
                )
            }

            PowerOfAttorney(
                header = header,
                body = PowerOfAttorneyBody(
                    bodyItemList
                )
            )
        }
}