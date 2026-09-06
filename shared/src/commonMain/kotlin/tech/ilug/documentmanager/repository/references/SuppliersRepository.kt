package tech.ilug.documentmanager.repository.references

import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.deleteWhere
import org.jetbrains.exposed.v1.jdbc.insertAndGetId
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.update
import tech.ilug.documentmanager.database.dbQuery
import tech.ilug.documentmanager.database.document.PowerOfAttorneyTables.SuppliersTable
import tech.ilug.documentmanager.model.PowerOfAttorney
import tech.ilug.documentmanager.model.Reference

class SuppliersRepository : Reference<PowerOfAttorney.Supplier>("suppliers") {

    private fun ResultRow.toSupplier() = PowerOfAttorney.Supplier(
        id = this[SuppliersTable.id].value,
        name = this[SuppliersTable.name]
    )

    override suspend fun getItem(id: Int): PowerOfAttorney.Supplier = dbQuery {
        SuppliersTable.selectAll().where { SuppliersTable.id eq id }.first().toSupplier()
    }

    override suspend fun getItems(): List<PowerOfAttorney.Supplier> = dbQuery {
        SuppliersTable.selectAll().map { it.toSupplier() }
    }

    override suspend fun newItem(): PowerOfAttorney.Supplier = createItem(PowerOfAttorney.Supplier())

    override suspend fun createItem(item: PowerOfAttorney.Supplier): PowerOfAttorney.Supplier = dbQuery {
        val newId = SuppliersTable.insertAndGetId { it[name] = item.name }.value
        item.copy(id = newId)
    }

    override suspend fun updateItem(item: PowerOfAttorney.Supplier): Unit = dbQuery {
        SuppliersTable.update({ SuppliersTable.id eq item.id }) {
            it[name] = item.name
        }
    }

    override suspend fun deleteItem(id: Int): Unit = dbQuery {
        SuppliersTable.deleteWhere { SuppliersTable.id eq id }
    }
}