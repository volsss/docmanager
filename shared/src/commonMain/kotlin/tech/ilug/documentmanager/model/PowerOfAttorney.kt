package tech.ilug.documentmanager.model

import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.todayIn
import kotlin.time.Clock

class PowerOfAttorney (
    val header: Header,
    val body: List<Body>
): Document {

    override fun replacements(): Map<String, String> {
        return header.replacements()
    }

    data class Header (
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
        ) + organization.replacements() +
                individual.replacements() +
                supplier.replacements()
    }

    data class Organization (
        override val id: Int,
        val name: String,
        val consumer: String,
        val payer: String,
        val account: String,
    ): Reference.Item(id) {
        constructor(
            name: String,
            consumer: String,
            payer: String,
            account: String,
        ) : this(-1, name, consumer, payer, account)
        constructor() : this("", "", "", "")
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
        override fun copyWithFields(fields: Map<String, Any>) = this.copy(
            name = (fields["organizationName"] ?: this.name) as String,
            consumer = (fields["organizationConsumer"] ?: this.consumer) as String,
            payer = (fields["organizationPayer"] ?: this.payer) as String,
            account = (fields["organizationAccount"] ?: this.account) as String
        )
    }

    data class Supplier (
        override val id: Int,
        val name: String
    ): Reference.Item(id) {
        constructor(name: String) : this(-1, name)
        constructor() : this("")
        fun replacements(): Map<String, String> = mapOf(
            "supplierName" to name
        )
        override fun asFields(): List<Reference.Field<out Any>> = listOf(
            Reference.Field("supplierName", name)
        )
        override fun copyWithFields(fields: Map<String, Any>) = this.copy(
            name = (fields["supplierName"] ?: this.name) as String
        )
    }

    data class Individual (
        override val id: Int,
        val job: String,
        val name: String,
        val series: String,
        val number: String,
        val issued: String,
        val date: LocalDate,
    ): Reference.Item(id) {
        constructor (
            job: String,
            name: String,
            series: String,
            number: String,
            issued: String,
            date: LocalDate
        ) : this(-1, name, job, series, number, issued, date)
        constructor () : this(
            "",
            "",
            "",
            "",
            "",
            Clock.System.todayIn(TimeZone.currentSystemDefault())
        )
        override fun asFields(): List<Reference.Field<out Any>> = listOf(
            Reference.Field("individualJob", job),
            Reference.Field("individualName", name),
            Reference.Field("individualSeries", series),
            Reference.Field("individualNumber", number),
            Reference.Field("individualIssued", issued),
            Reference.Field("individualDate", date)
        )

        override fun copyWithFields(fields: Map<String, Any>) = this.copy(
            job = (fields["individualJob"] ?: this.job) as String,
            name = (fields["individualName"] ?: this.name) as String,
            series = (fields["individualSeries"] ?: this.series) as String,
            number = (fields["individualNumber"] ?: this.number) as String,
            issued = (fields["individualIssued"] ?: this.issued) as String,
            date = (fields["individualDate"] ?: this.date) as LocalDate,
        )

        fun replacements(): Map<String, String> = mapOf(
            "individualJob" to job,
            "individualName" to name,
            "individualSeries" to series,
            "individualNumber" to number,
            "individualIssued" to issued,
            "individualDate" to date.format(LocalDate.Formats.ISO),
        )
    }

    data class Product (
        override val id: Int,
        val name: String,
    ): Reference.Item(id) {
        constructor(name: String) : this(-1, name)
        constructor() : this("")
        override fun asFields(): List<Reference.Field<out Any>> = listOf(
            Reference.Field("productName", name)
        )
        override fun copyWithFields(fields: Map<String, Any>) = this.copy(
            name = (fields["productName"] ?: this.name) as String
        )
    }

    data class Body (
        val id: Int,
        var count: String,
        var unit: String,
        val header: Header,
        val product: Product,
    )
}