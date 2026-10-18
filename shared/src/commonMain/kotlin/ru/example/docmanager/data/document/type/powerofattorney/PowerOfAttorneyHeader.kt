/*
 * Copyright (c) 2026 Andrew Z.
 * docmanager — a scalable document management system.
 */

package ru.example.docmanager.data.document.type.powerofattorney

import kotlinx.datetime.LocalDate
import ru.example.docmanager.data.document.header.DocumentHeader
import ru.example.docmanager.data.document.header.HeaderFieldType
import ru.example.docmanager.data.reference.Individual
import ru.example.docmanager.data.reference.Organization
import ru.example.docmanager.data.reference.Supplier
import ru.example.docmanager.ui.toRussianString

data class PowerOfAttorneyHeader(
    override val id: Int,
    override var number: Int,
    override var dischargeDate: LocalDate,
    var organization: Organization,
    var endDate: LocalDate,
    var individual: Individual,
    var supplier: Supplier,
    var supplierAgreement: String
) : DocumentHeader {
    override fun toMap(): Map<HeaderFieldType, String> = mapOf(
        HeaderFieldType.NUMBER to number.toString(),
        HeaderFieldType.DISCHARGE_DATE to dischargeDate.toRussianString(),
        HeaderFieldType.END_DATE to endDate.toRussianString(),
        HeaderFieldType.SUPPLIER_AGREEMENT to supplierAgreement,
    ) + organization.toMap() +
            individual.toMap() +
            supplier.toMap()
}