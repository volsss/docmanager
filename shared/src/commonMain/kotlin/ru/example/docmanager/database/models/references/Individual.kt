package ru.example.docmanager.database.models.references

import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.todayIn
import ru.example.docmanager.database.models.FieldType
import ru.example.docmanager.ui.Utils
import kotlin.time.Clock

data class Individual(
    override var id: Int = -1,
    val job: String = "",
    val name: String = "",
    val series: String = "",
    val number: String = "",
    val issued: String = "",
    val date: LocalDate = Clock.System.todayIn(TimeZone.currentSystemDefault())
) : Reference(id) {
    override fun toMap() = mapOf(
        FieldType.INDIVIDUAL_JOB to job,
        FieldType.INDIVIDUAL_NAME to name,
        FieldType.INDIVIDUAL_SERIES to series,
        FieldType.INDIVIDUAL_NUMBER to number,
        FieldType.INDIVIDUAL_ISSUED to issued,
        FieldType.INDIVIDUAL_DATE to date.format(Utils.DATE_FORMAT),
    )

    override fun copyWithFields(fields: Map<FieldType, String>) = copy(
        job = (fields[FieldType.INDIVIDUAL_JOB] ?: job),
        name = (fields[FieldType.INDIVIDUAL_NAME] ?: name),
        series = (fields[FieldType.INDIVIDUAL_SERIES] ?: series),
        number = (fields[FieldType.INDIVIDUAL_NUMBER] ?: number),
        issued = (fields[FieldType.INDIVIDUAL_ISSUED] ?: issued),
        date = (Utils.DATE_FORMAT.parseOrNull(
            fields[FieldType.INDIVIDUAL_DATE] ?: ""
        ) ?: date),
    )
}