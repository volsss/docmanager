package tech.ilug.documentmanager.repository.references

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.datetime.toJavaLocalDate
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.deleteWhere
import org.jetbrains.exposed.v1.jdbc.insertAndGetId
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.jetbrains.exposed.v1.jdbc.update
import tech.ilug.documentmanager.database.document.PowerOfAttorneyTables
import tech.ilug.documentmanager.database.document.PowerOfAttorneyTables.IndividualsTable.date
import tech.ilug.documentmanager.database.document.PowerOfAttorneyTables.IndividualsTable.issued
import tech.ilug.documentmanager.database.document.PowerOfAttorneyTables.IndividualsTable.job
import tech.ilug.documentmanager.database.document.PowerOfAttorneyTables.IndividualsTable.number
import tech.ilug.documentmanager.database.document.PowerOfAttorneyTables.IndividualsTable.series
import tech.ilug.documentmanager.model.PowerOfAttorney
import tech.ilug.documentmanager.model.Reference

class SuppliersRepository : Reference<PowerOfAttorney.Supplier>("suppliers") {

    override suspend fun getItem(id: Int) = withContext(Dispatchers.IO) {
        transaction {
            PowerOfAttorney.Supplier(
                id = id,
                name = PowerOfAttorneyTables.SuppliersTable
                    .selectAll()
                    .where(PowerOfAttorneyTables.SuppliersTable.id eq id)
                    .first()[PowerOfAttorneyTables.SuppliersTable.name]
            )
        }
    }

    override suspend fun getItems() = withContext(Dispatchers.IO) {
        transaction {
            PowerOfAttorneyTables.SuppliersTable.selectAll().map {
                PowerOfAttorney.Supplier(
                    id = it[PowerOfAttorneyTables.SuppliersTable.id].value,
                    name = it[PowerOfAttorneyTables.SuppliersTable.name]
                )
            }
        }
    }

    override suspend fun newItem() = createItem(PowerOfAttorney.Supplier())

    override suspend fun createItem (
        item: PowerOfAttorney.Supplier
    ) = withContext(Dispatchers.IO) {
        transaction {
            item.copy (
                id = PowerOfAttorneyTables.SuppliersTable.insertAndGetId {
                    it[name] = item.name
                }.value
            )
        }
    }

    override suspend fun updateItem(item: PowerOfAttorney.Supplier) {
        withContext(Dispatchers.IO)  {
            transaction {
                PowerOfAttorneyTables.SuppliersTable
                    .update({ PowerOfAttorneyTables.SuppliersTable.id eq item.id }) {
                        it[name] = item.name
                    }
            }
        }
    }

    override suspend fun deleteItem(id: Int) {
        withContext(Dispatchers.IO)  {
            transaction {
                PowerOfAttorneyTables.SuppliersTable.deleteWhere {
                    PowerOfAttorneyTables.SuppliersTable.id eq id
                }
            }
        }
    }

}