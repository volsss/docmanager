package ru.example.docmanager.database.repositories.references

import kotlinx.datetime.toJavaLocalDate
import kotlinx.datetime.toKotlinLocalDate
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.deleteWhere
import org.jetbrains.exposed.v1.jdbc.insertAndGetId
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.update
import ru.example.docmanager.database.dbQuery
import ru.example.docmanager.database.tables.PowerOfAttorneyTables.IndividualsTable
import ru.example.docmanager.database.models.references.Individual

class IndividualsRepository : ReferenceRepository<Individual>("individuals") {

    private fun ResultRow.toIndividual() = Individual(
        id = this[IndividualsTable.id].value,
        job = this[IndividualsTable.job],
        name = this[IndividualsTable.name],
        series = this[IndividualsTable.series],
        number = this[IndividualsTable.number],
        issued = this[IndividualsTable.issued],
        date = this[IndividualsTable.date].toKotlinLocalDate()
    )

    override suspend fun getItem(id: Int): Individual = dbQuery {
        IndividualsTable.selectAll().where { IndividualsTable.id eq id }.first().toIndividual()
    }

    override suspend fun getItems(): List<Individual> = dbQuery {
        IndividualsTable.selectAll().map { it.toIndividual() }
    }

    override suspend fun newItem(): Individual = createItem(Individual())

    override suspend fun createItem(item: Individual): Individual = dbQuery {
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

    override suspend fun updateItem(item: Individual): Unit = dbQuery {
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