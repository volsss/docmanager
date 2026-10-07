package ru.example.docmanager.document.header

import kotlinx.datetime.LocalDate

interface DocumentHeader {
    val id: Int
    var number: Int
    var dischargeDate: LocalDate
    fun toMap(): Map<HeaderFieldType, String>
}
