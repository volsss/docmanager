package ru.example.docmanager.documents

import kotlinx.datetime.LocalDate
import kotlinx.datetime.format
import ru.example.docmanager.documents.base.BodyFieldType
import ru.example.docmanager.documents.base.Document
import ru.example.docmanager.documents.base.DocumentBody
import ru.example.docmanager.documents.base.DocumentBodyItem
import ru.example.docmanager.documents.base.DocumentHeader
import ru.example.docmanager.documents.base.HeaderFieldType
import ru.example.docmanager.documents.base.WithBody
import ru.example.docmanager.references.Individual
import ru.example.docmanager.references.Organization
import ru.example.docmanager.references.Product
import ru.example.docmanager.references.Supplier

class PowerOfAttorney(
    override val header: Header,
    override val body: Body
): Document, WithBody<PowerOfAttorney.BodyItem> {
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
        override fun toMap(): Map<HeaderFieldType, String> = mapOf(
            HeaderFieldType.NUMBER to number.toString(),
            HeaderFieldType.DISCHARGE_DATE to dischargeDate.format(LocalDate.Formats.ISO),
            HeaderFieldType.END_DATE to endDate.format(LocalDate.Formats.ISO),
            HeaderFieldType.SUPPLIER_AGREEMENT to supplierAgreement,
        ) + organization.toMap() +
                individual.toMap() +
                supplier.toMap()
    }

    data class Body (
        override val items: List<BodyItem>
    ): DocumentBody<BodyItem> {
        override fun toMapByColumns() = mapOf(
            BodyFieldType.NUMBER_SORTED to List(items.size) { idx -> (idx + 1).toString() },
            BodyFieldType.PRODUCTS to items.map { it.product.name },
            BodyFieldType.UNIT to items.map { it.unit },
            BodyFieldType.COUNT to items.map { it.count }
        )
    }

    data class BodyItem (
        override val id: Int = 0,
        var count: String = "одна",
        var unit: String = "шт",
        val header: Header,
        val product: Product
    ): DocumentBodyItem
}