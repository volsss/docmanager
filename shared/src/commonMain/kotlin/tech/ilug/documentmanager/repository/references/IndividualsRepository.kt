package tech.ilug.documentmanager.repository.references

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toJavaLocalDate
import kotlinx.datetime.toKotlinLocalDate
import kotlinx.datetime.todayIn
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.deleteWhere
import org.jetbrains.exposed.v1.jdbc.insertAndGetId
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.jetbrains.exposed.v1.jdbc.update
import tech.ilug.documentmanager.database.document.PowerOfAttorneyTables
import tech.ilug.documentmanager.database.document.PowerOfAttorneyTables.OrganizationsTable.account
import tech.ilug.documentmanager.database.document.PowerOfAttorneyTables.OrganizationsTable.consumer
import tech.ilug.documentmanager.database.document.PowerOfAttorneyTables.OrganizationsTable.payer
import tech.ilug.documentmanager.model.PowerOfAttorney
import tech.ilug.documentmanager.model.Reference
import kotlin.time.Clock

class IndividualsRepository : Reference<PowerOfAttorney.Individual>("individuals") {

    override suspend fun getItem(id: Int) = withContext(Dispatchers.IO) {
        transaction {
            val row = PowerOfAttorneyTables.IndividualsTable
                .selectAll()
                .where(PowerOfAttorneyTables.IndividualsTable.id eq id)
                .first()
            PowerOfAttorney.Individual(
                id = id,
                name = row[PowerOfAttorneyTables.IndividualsTable.name],
                job = row[PowerOfAttorneyTables.IndividualsTable.job],
                series = row[PowerOfAttorneyTables.IndividualsTable.series],
                number = row[PowerOfAttorneyTables.IndividualsTable.number],
                issued = row[PowerOfAttorneyTables.IndividualsTable.issued],
                date = row[PowerOfAttorneyTables.IndividualsTable.date].toKotlinLocalDate(),
            )
        }
    }

    override suspend fun getItems() = withContext(Dispatchers.IO) {
        transaction {
            PowerOfAttorneyTables.IndividualsTable.selectAll().map {
                PowerOfAttorney.Individual(
                    id = it[PowerOfAttorneyTables.IndividualsTable.id].value,
                    job = it[PowerOfAttorneyTables.IndividualsTable.job],
                    name = it[PowerOfAttorneyTables.IndividualsTable.name],
                    series = it[PowerOfAttorneyTables.IndividualsTable.series],
                    number = it[PowerOfAttorneyTables.IndividualsTable.number],
                    issued = it[PowerOfAttorneyTables.IndividualsTable.issued],
                    date = it[PowerOfAttorneyTables.IndividualsTable.date].toKotlinLocalDate()
                )
            }
        }
    }

    override suspend fun newItem() = createItem (PowerOfAttorney.Individual())

    override suspend fun createItem (
        item: PowerOfAttorney.Individual
    ) = withContext(Dispatchers.IO) {
        transaction {
            item.copy (
                id = PowerOfAttorneyTables.IndividualsTable.insertAndGetId {
                    it[name] = item.name
                    it[job] = item.job
                    it[series] = item.series
                    it[number] = item.number
                    it[issued] = item.issued
                    it[date] = item.date.toJavaLocalDate()
                }.value
            )
        }
    }

    override suspend fun updateItem(item: PowerOfAttorney.Individual) {
        withContext(Dispatchers.IO)  {
            transaction {
                val updated = PowerOfAttorneyTables.IndividualsTable
                    .update({ PowerOfAttorneyTables.IndividualsTable.id eq item.id }) {
                        it[name] = item.name
                        it[job] = item.job
                        it[series] = item.series
                        it[number] = item.number
                        it[issued] = item.issued
                        it[date] = item.date.toJavaLocalDate()
                    }
                println("UPDATED $updated ROWS")
            }
        }
    }


    override suspend fun deleteItem(id: Int) {
        withContext(Dispatchers.IO)  {
            transaction {
                PowerOfAttorneyTables.IndividualsTable.deleteWhere {
                    PowerOfAttorneyTables.IndividualsTable.id eq id
                }
            }
        }
    }

}