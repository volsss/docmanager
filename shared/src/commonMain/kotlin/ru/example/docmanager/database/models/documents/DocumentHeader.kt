package ru.example.docmanager.database.models.documents

import kotlinx.datetime.LocalDate
import ru.example.docmanager.database.models.FieldType

interface DocumentHeader {
    val id: Int
    var number: Int
    var dischargeDate: LocalDate
    fun toMap(): Map<FieldType, String>
}
