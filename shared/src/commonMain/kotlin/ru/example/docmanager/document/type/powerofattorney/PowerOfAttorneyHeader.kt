package ru.example.docmanager.document.type.powerofattorney

import kotlinx.datetime.LocalDate
import kotlinx.datetime.format
import ru.example.docmanager.document.header.DocumentHeader
import ru.example.docmanager.document.header.HeaderFieldType
import ru.example.docmanager.reference.Individual
import ru.example.docmanager.reference.Organization
import ru.example.docmanager.reference.Supplier

data class PowerOfAttorneyHeader (
    override val id: Int,
    override var number: Int,
    override var dischargeDate: LocalDate,
    var organization: Organization,
    var endDate: LocalDate,
    var individual: Individual,
    var supplier: Supplier,
    var supplierAgreement: String
): DocumentHeader {
    override fun toMap(): Map<HeaderFieldType, String> = mapOf(
        HeaderFieldType.NUMBER to number.toString(),
        HeaderFieldType.DISCHARGE_DATE to dischargeDate.format(LocalDate.Formats.ISO),
        HeaderFieldType.END_DATE to endDate.format(LocalDate.Formats.ISO),
        HeaderFieldType.SUPPLIER_AGREEMENT to supplierAgreement,
    ) + organization.toMap() +
            individual.toMap() +
            supplier.toMap()
}