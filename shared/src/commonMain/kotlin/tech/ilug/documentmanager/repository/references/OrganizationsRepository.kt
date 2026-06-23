package tech.ilug.documentmanager.repository.references

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.deleteWhere
import org.jetbrains.exposed.v1.jdbc.insertAndGetId
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.jetbrains.exposed.v1.jdbc.update
import tech.ilug.documentmanager.database.document.PowerOfAttorneyTables
import tech.ilug.documentmanager.model.PowerOfAttorney
import tech.ilug.documentmanager.model.Reference

class OrganizationsRepository : Reference<PowerOfAttorney.Organization>("organizations") {

    override suspend fun getItem(id: Int) = withContext(Dispatchers.IO) {
        transaction {
            val row = PowerOfAttorneyTables.OrganizationsTable
                .selectAll()
                .where(PowerOfAttorneyTables.OrganizationsTable.id eq id)
                .first()
            PowerOfAttorney.Organization(
                id = id,
                name = row[PowerOfAttorneyTables.OrganizationsTable.name],
                consumer = row[PowerOfAttorneyTables.OrganizationsTable.name],
                payer = row[PowerOfAttorneyTables.OrganizationsTable.name],
                account = row[PowerOfAttorneyTables.OrganizationsTable.name]
            )
        }
    }

    override suspend fun getItems() = withContext(Dispatchers.IO) {
        transaction {
            PowerOfAttorneyTables.OrganizationsTable.selectAll().map {
                PowerOfAttorney.Organization(
                    id = it[PowerOfAttorneyTables.OrganizationsTable.id].value,
                    name = it[PowerOfAttorneyTables.OrganizationsTable.name],
                    consumer = it[PowerOfAttorneyTables.OrganizationsTable.consumer],
                    payer = it[PowerOfAttorneyTables.OrganizationsTable.payer],
                    account = it[PowerOfAttorneyTables.OrganizationsTable.account]
                )
            }
        }
    }

    override suspend fun newItem() = createItem(PowerOfAttorney.Organization())

    override suspend fun createItem (
        item: PowerOfAttorney.Organization
    ) = withContext(Dispatchers.IO) {
        transaction {
            item.copy (
                id = PowerOfAttorneyTables.OrganizationsTable.insertAndGetId {
                    it[name] = item.name
                    it[consumer] = item.consumer
                    it[payer] = item.payer
                    it[account] = item.account
                }.value
            )
        }
    }

    override suspend fun updateItem(
        item: PowerOfAttorney.Organization
    ) {
        withContext(Dispatchers.IO)  {
            transaction {
                PowerOfAttorneyTables.OrganizationsTable
                    .update({ PowerOfAttorneyTables.OrganizationsTable.id eq item.id }) {
                        it[name] = item.name
                        it[consumer] = item.consumer
                        it[payer] = item.payer
                        it[account] = item.account
                    }
            }
        }
    }

    override suspend fun deleteItem(id: Int) {
        withContext(Dispatchers.IO)  {
            transaction {
                PowerOfAttorneyTables.OrganizationsTable.deleteWhere {
                    PowerOfAttorneyTables.OrganizationsTable.id eq id
                }
            }
        }
    }

}