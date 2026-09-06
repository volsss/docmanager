package tech.ilug.documentmanager.model

import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.todayIn
import kotlin.time.Clock

class PowerOfAttorney(
    val header: Header,
    val body: List<Body>
) : Document {

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
    ) {
        fun replacements(): Map<String, String> = mapOf(
            "number" to number.toString(),
            "dischargeDate" to dischargeDate.format(LocalDate.Formats.ISO),
            "endDate" to endDate.format(LocalDate.Formats.ISO),
            "supplierAgreement" to supplierAgreement,
        ) + organization.replacements() + individual.replacements() + supplier.replacements()
    }

    data class Organization(
        override val id: Int = -1,
        val name: String = "",
        val consumer: String = "",
        val payer: String = "",
        val account: String = ""
    ) : Reference.Item(id) {
        fun replacements(): Map<String, String> = mapOf(
            "organizationName" to name,
            "organizationConsumer" to consumer,
            "organizationPayer" to payer,
            "organizationAccount" to account,
        )

        override fun asFields(): List<Reference.Field<out Any>> = listOf(
            Reference.Field("organizationName", name),
            Reference.Field("organizationConsumer", consumer),
            Reference.Field("organizationPayer", payer),
            Reference.Field("organizationAccount", account)
        )

        override fun copyWithFields(fields: Map<String, Any>) = copy(
            name = (fields["organizationName"] ?: name) as String,
            consumer = (fields["organizationConsumer"] ?: consumer) as String,
            payer = (fields["organizationPayer"] ?: payer) as String,
            account = (fields["organizationAccount"] ?: account) as String
        )
    }

    data class Supplier(
        override val id: Int = -1,
        val name: String = ""
    ) : Reference.Item(id) {
        fun replacements(): Map<String, String> = mapOf("supplierName" to name)
        override fun asFields(): List<Reference.Field<out Any>> = listOf(Reference.Field("supplierName", name))
        override fun copyWithFields(fields: Map<String, Any>) = copy(
            name = (fields["supplierName"] ?: name) as String
        )
    }

    data class Individual(
        override val id: Int = -1,
        val job: String = "",
        val name: String = "",
        val series: String = "",
        val number: String = "",
        val issued: String = "",
        val date: LocalDate = Clock.System.todayIn(TimeZone.currentSystemDefault())
    ) : Reference.Item(id) {
        fun replacements(): Map<String, String> = mapOf(
            "individualJob" to job,
            "individualName" to name,
            "individualSeries" to series,
            "individualNumber" to number,
            "individualIssued" to issued,
            "individualDate" to date.format(LocalDate.Formats.ISO),
        )

        override fun asFields(): List<Reference.Field<out Any>> = listOf(
            Reference.Field("individualJob", job),
            Reference.Field("individualName", name),
            Reference.Field("individualSeries", series),
            Reference.Field("individualNumber", number),
            Reference.Field("individualIssued", issued),
            Reference.Field("individualDate", date)
        )

        override fun copyWithFields(fields: Map<String, Any>) = copy(
            job = (fields["individualJob"] ?: job) as String,
            name = (fields["individualName"] ?: name) as String,
            series = (fields["individualSeries"] ?: series) as String,
            number = (fields["individualNumber"] ?: number) as String,
            issued = (fields["individualIssued"] ?: issued) as String,
            date = (fields["individualDate"] ?: date) as LocalDate,
        )
    }

    data class Product(
        override val id: Int = -1,
        val name: String = ""
    ) : Reference.Item(id) {
        override fun asFields(): List<Reference.Field<out Any>> = listOf(Reference.Field("productName", name))
        override fun copyWithFields(fields: Map<String, Any>) = copy(
            name = (fields["productName"] ?: name) as String
        )
    }

    data class Body(
        val id: Int = 0,
        var count: String = "одна",
        var unit: String = "шт",
        val header: Header,
        val product: Product
    )
}