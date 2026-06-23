package tech.ilug.documentmanager.repository.references

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.deleteWhere
import org.jetbrains.exposed.v1.jdbc.insertAndGetId
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.jetbrains.exposed.v1.jdbc.update
import org.jetbrains.exposed.v1.jdbc.upsert
import tech.ilug.documentmanager.database.document.PowerOfAttorneyTables
import tech.ilug.documentmanager.model.PowerOfAttorney
import tech.ilug.documentmanager.model.Reference

class ProductsRepository : Reference<PowerOfAttorney.Product>("products") {

    override suspend fun getItem (
        id: Int
    ): PowerOfAttorney.Product = withContext(Dispatchers.IO) {
        transaction {
            PowerOfAttorney.Product(
                id = id,
                name = PowerOfAttorneyTables.ProductsTable
                    .selectAll()
                    .where(PowerOfAttorneyTables.ProductsTable.id eq id)
                    .first()[PowerOfAttorneyTables.ProductsTable.name]
            )
        }
    }

    override suspend fun getItems() = withContext(Dispatchers.IO) {
        transaction {
            PowerOfAttorneyTables.ProductsTable.selectAll().map {
                PowerOfAttorney.Product(
                    id = it[PowerOfAttorneyTables.ProductsTable.id].value,
                    name = it[PowerOfAttorneyTables.ProductsTable.name]
                )
            }
        }
    }

    override suspend fun newItem() = createItem(PowerOfAttorney.Product())

    override suspend fun createItem(
        item: PowerOfAttorney.Product
    ) = withContext(Dispatchers.IO)  {
        transaction {
            item.copy(
                id = PowerOfAttorneyTables.ProductsTable.insertAndGetId {
                    it[name] = item.name
                }.value
            )
        }
    }

    override suspend fun updateItem(
        item: PowerOfAttorney.Product
    ) {
        withContext(Dispatchers.IO)  {
            transaction {
                PowerOfAttorneyTables.ProductsTable
                    .update({ PowerOfAttorneyTables.ProductsTable.id eq item.id }) {
                        it[name] = item.name
                    }
            }
        }
    }

    override suspend fun deleteItem(id: Int) {
        withContext(Dispatchers.IO)  {
            transaction {
                PowerOfAttorneyTables.ProductsTable.deleteWhere {
                    PowerOfAttorneyTables.ProductsTable.id eq id
                }
            }
        }
    }

}