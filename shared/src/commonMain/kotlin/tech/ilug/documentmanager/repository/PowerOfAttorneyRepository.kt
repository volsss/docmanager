package tech.ilug.documentmanager.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.datetime.toJavaLocalDate
import kotlinx.datetime.toKotlinLocalDate
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.deleteWhere
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import tech.ilug.documentmanager.database.document.PowerOfAttorneyTables
import tech.ilug.documentmanager.model.PowerOfAttorney
import tech.ilug.documentmanager.repository.references.IndividualsRepository
import tech.ilug.documentmanager.repository.references.OrganizationsRepository
import tech.ilug.documentmanager.repository.references.ProductsRepository
import tech.ilug.documentmanager.repository.references.SuppliersRepository

class PowerOfAttorneyRepository (
    val individualsRepository: IndividualsRepository,
    val organizationsRepository: OrganizationsRepository,
    val productsRepository: ProductsRepository,
    val suppliersRepository: SuppliersRepository
): DocumentRepository<PowerOfAttorney> {

    override suspend fun createDocument (
        document: PowerOfAttorney
    ) {
        withContext(Dispatchers.IO) {
            transaction {
                PowerOfAttorneyTables.HeadersTable.insert {
                    it[organizationId] = document.header.organization.id
                    it[number] = document.header.number
                    it[dischargeDate] = document.header.dischargeDate.toJavaLocalDate()
                    it[endDate] = document.header.endDate.toJavaLocalDate()
                    it[individualId] = document.header.individual.id
                    it[supplierId] = document.header.supplier.id
                    it[supplierAgreement] = document.header.supplierAgreement
                }
                document.body.forEach { createBody(it) }
            }
        }
    }

    override suspend fun updateDocument(document: PowerOfAttorney) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteDocument(id: Int) {
        withContext(Dispatchers.IO) {
            transaction {
                PowerOfAttorneyTables.HeadersTable.deleteWhere {
                    PowerOfAttorneyTables.HeadersTable.id eq id
                }
                PowerOfAttorneyTables.BodiesTable.deleteWhere {
                    PowerOfAttorneyTables.BodiesTable.headerId eq id
                }
            }
        }
    }

    override suspend fun getDocument(id: Int): PowerOfAttorney {
        val (headerRow, bodyRows) = withContext(Dispatchers.IO) {
            transaction {
                val header = PowerOfAttorneyTables.HeadersTable
                    .selectAll()
                    .where { PowerOfAttorneyTables.HeadersTable.id eq id }
                    .first()

                val bodies = PowerOfAttorneyTables.BodiesTable
                    .selectAll()
                    .where(PowerOfAttorneyTables.BodiesTable.headerId eq id)
                    .toList()

                header to bodies
            }
        }

        val organization = organizationsRepository.getItem(
            headerRow[PowerOfAttorneyTables.HeadersTable.organizationId]
        )
        val individual = individualsRepository.getItem(
            headerRow[PowerOfAttorneyTables.HeadersTable.individualId]
        )
        val supplier = suppliersRepository.getItem(
            headerRow[PowerOfAttorneyTables.HeadersTable.supplierId]
        )

        val header = PowerOfAttorney.Header (
            id = id,
            organization = organization,
            number = headerRow[PowerOfAttorneyTables.HeadersTable.number],
            dischargeDate = headerRow[PowerOfAttorneyTables.HeadersTable.dischargeDate].toKotlinLocalDate(),
            endDate = headerRow[PowerOfAttorneyTables.HeadersTable.endDate].toKotlinLocalDate(),
            individual = individual,
            supplier = supplier,
            supplierAgreement = headerRow[PowerOfAttorneyTables.HeadersTable.supplierAgreement]
        )

        val bodyList = bodyRows.map { row ->
            val product = productsRepository.getItem(row[PowerOfAttorneyTables.BodiesTable.productId])
            PowerOfAttorney.Body(
                id = row[PowerOfAttorneyTables.BodiesTable.id].value,
                count = row[PowerOfAttorneyTables.BodiesTable.count],
                unit = row[PowerOfAttorneyTables.BodiesTable.unit],
                header = header,
                product = product
            )
        }

        return PowerOfAttorney(header = header, body = bodyList)
    }

    fun createBody(body: PowerOfAttorney.Body) {
        PowerOfAttorneyTables.BodiesTable.insert {
            it[count] = body.count
            it[unit] = body.unit
            it[headerId] = body.header.id
            it[productId] = body.product.id
        }
    }
}