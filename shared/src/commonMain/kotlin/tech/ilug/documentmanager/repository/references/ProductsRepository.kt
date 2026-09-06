package tech.ilug.documentmanager.repository.references

import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.deleteWhere
import org.jetbrains.exposed.v1.jdbc.insertAndGetId
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.update
import tech.ilug.documentmanager.database.dbQuery
import tech.ilug.documentmanager.database.document.PowerOfAttorneyTables.ProductsTable
import tech.ilug.documentmanager.model.PowerOfAttorney
import tech.ilug.documentmanager.model.Reference

class ProductsRepository : Reference<PowerOfAttorney.Product>("products") {

    private fun ResultRow.toProduct() = PowerOfAttorney.Product(
        id = this[ProductsTable.id].value,
        name = this[ProductsTable.name]
    )

    override suspend fun getItem(id: Int): PowerOfAttorney.Product = dbQuery {
        ProductsTable.selectAll().where { ProductsTable.id eq id }.first().toProduct()
    }

    override suspend fun getItems(): List<PowerOfAttorney.Product> = dbQuery {
        ProductsTable.selectAll().map { it.toProduct() }
    }

    override suspend fun newItem(): PowerOfAttorney.Product = createItem(PowerOfAttorney.Product())

    override suspend fun createItem(item: PowerOfAttorney.Product): PowerOfAttorney.Product = dbQuery {
        val newId = ProductsTable.insertAndGetId { it[name] = item.name }.value
        item.copy(id = newId)
    }

    override suspend fun updateItem(item: PowerOfAttorney.Product): Unit = dbQuery {
        ProductsTable.update({ ProductsTable.id eq item.id }) {
            it[name] = item.name
        }
    }

    override suspend fun deleteItem(id: Int): Unit = dbQuery {
        ProductsTable.deleteWhere { ProductsTable.id eq id }
    }
}