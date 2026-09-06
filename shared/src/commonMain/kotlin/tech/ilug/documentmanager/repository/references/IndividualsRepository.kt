package tech.ilug.documentmanager.repository.references

import kotlinx.datetime.toJavaLocalDate
import kotlinx.datetime.toKotlinLocalDate
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.deleteWhere
import org.jetbrains.exposed.v1.jdbc.insertAndGetId
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.update
import tech.ilug.documentmanager.database.dbQuery
import tech.ilug.documentmanager.database.document.PowerOfAttorneyTables.IndividualsTable
import tech.ilug.documentmanager.model.PowerOfAttorney
import tech.ilug.documentmanager.model.Reference

class IndividualsRepository : Reference<PowerOfAttorney.Individual>("individuals") {

    private fun ResultRow.toIndividual() = PowerOfAttorney.Individual(
        id = this[IndividualsTable.id].value,
        job = this[IndividualsTable.job],
        name = this[IndividualsTable.name],
        series = this[IndividualsTable.series],
        number = this[IndividualsTable.number],
        issued = this[IndividualsTable.issued],
        date = this[IndividualsTable.date].toKotlinLocalDate()
    )

    override suspend fun getItem(id: Int): PowerOfAttorney.Individual = dbQuery {
        IndividualsTable.selectAll().where { IndividualsTable.id eq id }.first().toIndividual()
    }

    override suspend fun getItems(): List<PowerOfAttorney.Individual> = dbQuery {
        IndividualsTable.selectAll().map { it.toIndividual() }
    }

    override suspend fun newItem(): PowerOfAttorney.Individual = createItem(PowerOfAttorney.Individual())

    override suspend fun createItem(item: PowerOfAttorney.Individual): PowerOfAttorney.Individual = dbQuery {
        val newId = IndividualsTable.insertAndGetId {
            it[name] = item.name
            it[job] = item.job
            it[series] = item.series
            it[number] = item.number
            it[issued] = item.issued
            it[date] = item.date.toJavaLocalDate()
        }.value
        item.copy(id = newId)
    }

    override suspend fun updateItem(item: PowerOfAttorney.Individual): Unit = dbQuery {
        IndividualsTable.update({ IndividualsTable.id eq item.id }) {
            it[name] = item.name
            it[job] = item.job
            it[series] = item.series
            it[number] = item.number
            it[issued] = item.issued
            it[date] = item.date.toJavaLocalDate()
        }
    }

    override suspend fun deleteItem(id: Int): Unit = dbQuery {
        IndividualsTable.deleteWhere { IndividualsTable.id eq id }
    }
}