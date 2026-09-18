package ru.example.docmanager.database.repositories.references

import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.deleteWhere
import org.jetbrains.exposed.v1.jdbc.insertAndGetId
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.update
import ru.example.docmanager.database.dbQuery
import ru.example.docmanager.database.tables.PowerOfAttorneyTables.SuppliersTable
import ru.example.docmanager.database.models.references.Supplier

class SuppliersRepository : ReferenceRepository<Supplier>("suppliers") {

    private fun ResultRow.toSupplier() = Supplier(
        id = this[SuppliersTable.id].value,
        name = this[SuppliersTable.name]
    )

    override suspend fun getItem(id: Int): Supplier = dbQuery {
        SuppliersTable.selectAll().where { SuppliersTable.id eq id }.first().toSupplier()
    }

    override suspend fun getItems(): List<Supplier> = dbQuery {
        SuppliersTable.selectAll().map { it.toSupplier() }
    }

    override suspend fun newItem(): Supplier = createItem(Supplier())

    override suspend fun createItem(item: Supplier): Supplier = dbQuery {
        val newId = SuppliersTable.insertAndGetId { it[name] = item.name }.value
        item.copy(id = newId)
    }

    override suspend fun updateItem(item: Supplier): Unit = dbQuery {
        SuppliersTable.update({ SuppliersTable.id eq item.id }) {
            it[name] = item.name
        }
    }

    override suspend fun deleteItem(id: Int): Unit = dbQuery {
        SuppliersTable.deleteWhere { SuppliersTable.id eq id }
    }
}