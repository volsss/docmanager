/*
 * Copyright (c) 2026 Andrew Z.
 * docmanager — a scalable document management system.
 */

package ru.example.docmanager.data.document.type.powerofattorney

import kotlinx.datetime.LocalDate
import kotlinx.datetime.format
import ru.example.docmanager.data.document.header.DocumentHeader
import ru.example.docmanager.data.document.header.HeaderFieldType

data class PowerOfAttorneyHeader(
    override val id: Int,
    override var number: Int,
    override var dischargeDate: LocalDate,
    var organization: ru.example.docmanager.data.reference.Organization,
    var endDate: LocalDate,
    var individual: ru.example.docmanager.data.reference.Individual,
    var supplier: ru.example.docmanager.data.reference.Supplier,
    var supplierAgreement: String
) : DocumentHeader {
    override fun toMap(): Map<HeaderFieldType, String> = mapOf(
        HeaderFieldType.NUMBER to number.toString(),
        HeaderFieldType.DISCHARGE_DATE to dischargeDate.format(LocalDate.Formats.ISO),
        HeaderFieldType.END_DATE to endDate.format(LocalDate.Formats.ISO),
        HeaderFieldType.SUPPLIER_AGREEMENT to supplierAgreement,
    ) + organization.toMap() +
            individual.toMap() +
            supplier.toMap()
}