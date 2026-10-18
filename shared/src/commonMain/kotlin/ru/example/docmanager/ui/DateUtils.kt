/*
 * Copyright (c) 2026 Andrew Z.
 * docmanager — a scalable document management system.
 */

package ru.example.docmanager.ui

import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.format.char
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Instant

val DATE_FORMAT = LocalDate.Format {
    day(); char('.'); monthNumber(); char('.'); year()
}

fun String.parseRussianOrNull(): LocalDate? {
    return runCatching { DATE_FORMAT.parse(this) }.getOrNull()
}

fun LocalDate.toEpochMillisUtc(): Long {
    return this.atStartOfDayIn(TimeZone.UTC).toEpochMilliseconds()
}

fun Long.toLocalDateFromUtc(): LocalDate {
    return Instant.fromEpochMilliseconds(this)
        .toLocalDateTime(TimeZone.UTC)
        .date
}

fun LocalDate.toRussianString(): String {
    return DATE_FORMAT.format(this)
}