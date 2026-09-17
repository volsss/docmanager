package tech.ilug.documentmanager.database.models.documents

import kotlinx.datetime.LocalDate
import kotlinx.datetime.format
import tech.ilug.documentmanager.database.models.references.Individual
import tech.ilug.documentmanager.database.models.references.Organization
import tech.ilug.documentmanager.database.models.references.Product
import tech.ilug.documentmanager.database.models.references.Supplier

class PowerOfAttorney(
    val header: Header,
    val body: List<Body>
) : DocumentModel {

    override fun replacements(): Map<String, String> = header.replacements()

    data class Header(
        val id: Int,
        var organization: Organization,
        var number: Int,
        var dischargeDate: LocalDate,
        var endDate: LocalDate,
        var individual: Individual,
        var supplier: Supplier,
        var supplierAgreement: String
    ): DocumentHeaderModel {
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