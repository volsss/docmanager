package tech.ilug.documentmanager.repository.documents

import kotlinx.datetime.toJavaLocalDate
import kotlinx.datetime.toKotlinLocalDate
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.deleteWhere
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.insertAndGetId
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.update
import tech.ilug.documentmanager.database.dbQuery
import tech.ilug.documentmanager.database.document.PowerOfAttorneyTables.BodiesTable
import tech.ilug.documentmanager.database.document.PowerOfAttorneyTables.HeadersTable
import tech.ilug.documentmanager.model.PowerOfAttorney
import tech.ilug.documentmanager.repository.references.IndividualsRepository
import tech.ilug.documentmanager.repository.references.OrganizationsRepository
import tech.ilug.documentmanager.repository.references.ProductsRepository
import tech.ilug.documentmanager.repository.references.SuppliersRepository

class PowerOfAttorneyRepository(
    val individualsRepository: IndividualsRepository,
    val organizationsRepository: OrganizationsRepository,
    val productsRepository: ProductsRepository,
    val suppliersRepository: SuppliersRepository
) : DocumentRepository<PowerOfAttorney> {

    override suspend fun createDocument(document: PowerOfAttorney): Unit = dbQuery {
        val headerId = HeadersTable.insertAndGetId {
            it[organizationId] = document.header.organization.id
            it[number] = document.header.number
            it[dischargeDate] = document.header.dischargeDate.toJavaLocalDate()
            it[endDate] = document.header.endDate.toJavaLocalDate()
            it[individualId] = document.header.individual.id
            it[supplierId] = document.header.supplier.id
            it[supplierAgreement] = document.header.supplierAgreement
        }.value
        document.body.forEach { body ->
            BodiesTable.insert {
                it[count] = body.count
                it[unit] = body.unit
                it[BodiesTable.headerId] = headerId
                it[productId] = body.product.id
            }
        }
    }

    override suspend fun updateDocument(document: PowerOfAttorney): Unit = dbQuery {
        HeadersTable.update({ HeadersTable.id eq document.header.id }) {
            it[organizationId] = document.header.organization.id
            it[number] = document.header.number
            it[dischargeDate] = document.header.dischargeDate.toJavaLocalDate()
            it[endDate] = document.header.endDate.toJavaLocalDate()
            it[individualId] = document.header.individual.id
            it[supplierId] = document.header.supplier.id
            it[supplierAgreement] = document.header.supplierAgreement
        }
        BodiesTable.deleteWhere { BodiesTable.headerId eq document.header.id }
        document.body.forEach { body ->
            BodiesTable.insert {
                it[count] = body.count
                it[unit] = body.unit
                it[BodiesTable.headerId] = document.header.id
                it[productId] = body.product.id
            }
        }
    }

    override suspend fun deleteDocument(id: Int): Unit = dbQuery {
        BodiesTable.deleteWhere { BodiesTable.headerId eq id }
        HeadersTable.deleteWhere { HeadersTable.id eq id }
    }

    override suspend fun getAllDocuments(): List<PowerOfAttorney> {
        val headerIds = dbQuery { HeadersTable.selectAll().map { it[HeadersTable.id].value } }
        return headerIds.map { getDocument(it) }
    }

    override suspend fun getDocument(id: Int): PowerOfAttorney {
        val (headerRow, bodyRows) = dbQuery {
            val header = HeadersTable.selectAll().where { HeadersTable.id eq id }.first()
            val bodies = BodiesTable.selectAll().where { BodiesTable.headerId eq id }.toList()
            header to bodies
        }

        val organization = organizationsRepository.getItem(headerRow[HeadersTable.organizationId])
        val individual = individualsRepository.getItem(headerRow[HeadersTable.individualId])
        val supplier = suppliersRepository.getItem(headerRow[HeadersTable.supplierId])

        val header = PowerOfAttorney.Header(
            id = id,
            organization = organization,
            number = headerRow[HeadersTable.number],
            dischargeDate = headerRow[HeadersTable.dischargeDate].toKotlinLocalDate(),
            endDate = headerRow[HeadersTable.endDate].toKotlinLocalDate(),
            individual = individual,
            supplier = supplier,
            supplierAgreement = headerRow[HeadersTable.supplierAgreement]
        )

        val bodyList = bodyRows.map { row ->
            val product = productsRepository.getItem(row[BodiesTable.productId])
            PowerOfAttorney.Body(
                id = row[BodiesTable.id].value,
                count = row[BodiesTable.count],
                unit = row[BodiesTable.unit],
                header = header,
                product = product
            )
        }

        return PowerOfAttorney(header = header, body = bodyList)
    }
}