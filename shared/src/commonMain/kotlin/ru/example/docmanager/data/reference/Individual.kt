/*
 * Copyright (c) 2026 Andrew Z.
 * docmanager — a scalable document management system.
 */

package ru.example.docmanager.data.reference

import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import ru.example.docmanager.data.document.header.HeaderFieldType
import ru.example.docmanager.ui.parseRussianOrNull
import ru.example.docmanager.ui.toRussianString
import kotlin.time.Clock

data class Individual(
    override var id: Int = -1,
    val job: String = "",
    val name: String = "",
    val series: String = "",
    val number: String = "",
    val issued: String = "",
    val date: LocalDate = Clock.System.todayIn(TimeZone.currentSystemDefault())
) : Reference(id, ReferenceType.INDIVIDUAL) {
    override fun toMap() = mapOf(
        HeaderFieldType.INDIVIDUAL_JOB to job,
        HeaderFieldType.INDIVIDUAL_NAME to name,
        HeaderFieldType.INDIVIDUAL_SERIES to series,
        HeaderFieldType.INDIVIDUAL_NUMBER to number,
        HeaderFieldType.INDIVIDUAL_ISSUED to issued,
        HeaderFieldType.INDIVIDUAL_DATE to date.toRussianString(),
    )

    override fun copyWithFields(fields: Map<HeaderFieldType, String>) = copy(
        job = (fields[HeaderFieldType.INDIVIDUAL_JOB] ?: job),
        name = (fields[HeaderFieldType.INDIVIDUAL_NAME] ?: name),
        series = (fields[HeaderFieldType.INDIVIDUAL_SERIES] ?: series),
        number = (fields[HeaderFieldType.INDIVIDUAL_NUMBER] ?: number),
        issued = (fields[HeaderFieldType.INDIVIDUAL_ISSUED] ?: issued),
        date = (fields[HeaderFieldType.INDIVIDUAL_DATE] ?: "").parseRussianOrNull() ?: date
    )
}