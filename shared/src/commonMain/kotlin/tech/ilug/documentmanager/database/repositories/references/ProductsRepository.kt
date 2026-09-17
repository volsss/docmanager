package tech.ilug.documentmanager.database.repositories.references

import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.deleteWhere
import org.jetbrains.exposed.v1.jdbc.insertAndGetId
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.update
import tech.ilug.documentmanager.database.dbQuery
import tech.ilug.documentmanager.database.tables.PowerOfAttorneyTables.ProductsTable
import tech.ilug.documentmanager.database.models.references.Product

class ProductsRepository : ReferenceRepository<Product>("products") {

    private fun ResultRow.toProduct() = Product(
        id = this[ProductsTable.id].value,
        name = this[ProductsTable.name]
    )

    override suspend fun getItem(id: Int): Product = dbQuery {
        ProductsTable.selectAll().where { ProductsTable.id eq id }.first().toProduct()
    }

    override suspend fun getItems(): List<Product> = dbQuery {
        ProductsTable.selectAll().map { it.toProduct() }
    }

    override suspend fun newItem(): Product = createItem(Product())

    override suspend fun createItem(item: Product): Product = dbQuery {
        val newId = ProductsTable.insertAndGetId { it[name] = item.name }.value
        item.copy(id = newId)
    }

    override suspend fun updateItem(item: Product): Unit = dbQuery {
        ProductsTable.update({ ProductsTable.id eq item.id }) {
            it[name] = item.name
        }
    }

    override suspend fun deleteItem(id: Int): Unit = dbQuery {
        ProductsTable.deleteWhere { ProductsTable.id eq id }
    }
}