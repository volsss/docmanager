package ru.example.docmanager.database.models.documents

import kotlinx.datetime.LocalDate
import kotlinx.datetime.format
import ru.example.docmanager.database.models.references.Individual
import ru.example.docmanager.database.models.references.Organization
import ru.example.docmanager.database.models.references.Product
import ru.example.docmanager.database.models.references.Supplier

class PowerOfAttorney(
    override val header: Header,
    val body: List<Body>
) : Document {

    override fun replacements(): Map<String, String> = header.replacements()

    data class Header(
        override val id: Int,
        override var number: Int,
        override var dischargeDate: LocalDate,
        var organization: Organization,
        var endDate: LocalDate,
        var individual: Individual,
        var supplier: Supplier,
        var supplierAgreement: String
    ): DocumentHeader {
        override fun replacements(): Map<String, String> = mapOf(
            "number" to number.toString(),
            "dischargeDate" to dischargeDate.format(LocalDate.Formats.ISO),
            "endDate" to endDate.format(LocalDate.Formats.ISO),
            "supplierAgreement" to supplierAgreement,
        ) + organization.replacements() + individual.replacements() + supplier.replacements()
    }

    data class Body (
        val id: Int = 0,
        var count: String = "одна",
        var unit: String = "шт",
        val header: Header,
        val product: Product
    )
}