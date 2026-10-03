package ru.example.docmanager.database.models.references

import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.todayIn
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
    override fun toMap(): Map<String, String> = mapOf(
        "individualJob" to job,
        "individualName" to name,
        "individualSeries" to series,
        "individualNumber" to number,
        "individualIssued" to issued,
        "individualDate" to date.format(Utils.DATE_FORMAT),
    )

    override fun copyWithFields(fields: Map<String, Any>) = copy(
        job = (fields["individualJob"] ?: job) as String,
        name = (fields["individualName"] ?: name) as String,
        series = (fields["individualSeries"] ?: series) as String,
        number = (fields["individualNumber"] ?: number) as String,
        issued = (fields["individualIssued"] ?: issued) as String,
        date = (Utils.DATE_FORMAT.parseOrNull(
            fields["individualDate"] as CharSequence
        ) ?: date),
    )
}