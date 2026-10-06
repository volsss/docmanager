package ru.example.docmanager.references

import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.todayIn
import ru.example.docmanager.documents.base.HeaderFieldType
import ru.example.docmanager.references.base.Reference
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
        HeaderFieldType.INDIVIDUAL_JOB to job,
        HeaderFieldType.INDIVIDUAL_NAME to name,
        HeaderFieldType.INDIVIDUAL_SERIES to series,
        HeaderFieldType.INDIVIDUAL_NUMBER to number,
        HeaderFieldType.INDIVIDUAL_ISSUED to issued,
        HeaderFieldType.INDIVIDUAL_DATE to date.format(Utils.DATE_FORMAT),
    )

    override fun copyWithFields(fields: Map<HeaderFieldType, String>) = copy(
        job = (fields[HeaderFieldType.INDIVIDUAL_JOB] ?: job),
        name = (fields[HeaderFieldType.INDIVIDUAL_NAME] ?: name),
        series = (fields[HeaderFieldType.INDIVIDUAL_SERIES] ?: series),
        number = (fields[HeaderFieldType.INDIVIDUAL_NUMBER] ?: number),
        issued = (fields[HeaderFieldType.INDIVIDUAL_ISSUED] ?: issued),
        date = (Utils.DATE_FORMAT.parseOrNull(
            fields[HeaderFieldType.INDIVIDUAL_DATE] ?: ""
        ) ?: date),
    )
}