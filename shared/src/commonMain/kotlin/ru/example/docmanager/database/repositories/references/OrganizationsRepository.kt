package ru.example.docmanager.database.repositories.references

import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.deleteWhere
import org.jetbrains.exposed.v1.jdbc.insertAndGetId
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.update
import ru.example.docmanager.database.dbQuery
import ru.example.docmanager.database.tables.PowerOfAttorneyTables.OrganizationsTable
import ru.example.docmanager.database.models.references.Organization

class OrganizationsRepository : ReferenceRepository<Organization>("organizations") {

    private fun ResultRow.toOrganization() = Organization(
        id = this[OrganizationsTable.id].value,
        name = this[OrganizationsTable.name],
        consumer = this[OrganizationsTable.consumer],
        payer = this[OrganizationsTable.payer],
        account = this[OrganizationsTable.account]
    )

    override suspend fun getItem(id: Int): Organization = dbQuery {
        OrganizationsTable.selectAll().where { OrganizationsTable.id eq id }.first().toOrganization()
    }

    override suspend fun getItems(): List<Organization> = dbQuery {
        OrganizationsTable.selectAll().map { it.toOrganization() }
    }

    override suspend fun newItem(): Organization = createItem(Organization())

    override suspend fun createItem(item: Organization): Organization = dbQuery {
        val newId = OrganizationsTable.insertAndGetId {
            it[name] = item.name
            it[consumer] = item.consumer
            it[payer] = item.payer
            it[account] = item.account
        }.value
        item.copy(id = newId)
    }

    override suspend fun updateItem(item: Organization): Unit = dbQuery {
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