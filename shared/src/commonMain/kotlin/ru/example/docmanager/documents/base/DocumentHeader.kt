package ru.example.docmanager.documents.base

import kotlinx.datetime.LocalDate

interface DocumentHeader {
    val id: Int
    var number: Int
    var dischargeDate: LocalDate
    fun toMap(): Map<HeaderFieldType, String>
}
