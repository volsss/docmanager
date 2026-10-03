package ru.example.docmanager.database.models.documents

import kotlinx.datetime.LocalDate
import ru.example.docmanager.database.models.references.Organization

interface DocumentHeader {
    val id: Int
    var organization: Organization
    var number: Int
    var dischargeDate: LocalDate
    fun replacements(): Map<String, String>
}