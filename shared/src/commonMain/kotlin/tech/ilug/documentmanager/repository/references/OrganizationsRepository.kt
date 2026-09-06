package tech.ilug.documentmanager.repository.references

import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.deleteWhere
import org.jetbrains.exposed.v1.jdbc.insertAndGetId
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.update
import tech.ilug.documentmanager.database.dbQuery
import tech.ilug.documentmanager.database.document.PowerOfAttorneyTables.OrganizationsTable
import tech.ilug.documentmanager.model.PowerOfAttorney
import tech.ilug.documentmanager.model.Reference

class OrganizationsRepository : Reference<PowerOfAttorney.Organization>("organizations") {

    private fun ResultRow.toOrganization() = PowerOfAttorney.Organization(
        id = this[OrganizationsTable.id].value,
        name = this[OrganizationsTable.name],
        consumer = this[OrganizationsTable.consumer],
        payer = this[OrganizationsTable.payer],
        account = this[OrganizationsTable.account]
    )

    override suspend fun getItem(id: Int): PowerOfAttorney.Organization = dbQuery {
        OrganizationsTable.selectAll().where { OrganizationsTable.id eq id }.first().toOrganization()
    }

    override suspend fun getItems(): List<PowerOfAttorney.Organization> = dbQuery {
        OrganizationsTable.selectAll().map { it.toOrganization() }
    }

    override suspend fun newItem(): PowerOfAttorney.Organization = createItem(PowerOfAttorney.Organization())

    override suspend fun createItem(item: PowerOfAttorney.Organization): PowerOfAttorney.Organization = dbQuery {
        val newId = OrganizationsTable.insertAndGetId {
            it[name] = item.name
            it[consumer] = item.consumer
            it[payer] = item.payer
            it[account] = item.account
        }.value
        item.copy(id = newId)
    }

    override suspend fun updateItem(item: PowerOfAttorney.Organization): Unit = dbQuery {
        OrganizationsTable.update({ OrganizationsTable.id eq item.id }) {
            it[name] = item.name
            it[consumer] = item.consumer
            it[payer] = item.payer
            it[account] = item.account
        }
    }

    override suspend fun deleteItem(id: Int): Unit = dbQuery {
        OrganizationsTable.deleteWhere { OrganizationsTable.id eq id }
    }
}