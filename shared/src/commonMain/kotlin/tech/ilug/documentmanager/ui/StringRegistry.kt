package tech.ilug.documentmanager.ui

import documentmanager.shared.generated.resources.*
import org.jetbrains.compose.resources.StringResource

object StringRegistry {
    private val map = mapOf(
        "individuals" to Res.string.individuals,
        "individualJob" to Res.string.individualJob,
        "individualName" to Res.string.individualName,
        "individualSeries" to Res.string.individualSeries,
        "individualNumber" to Res.string.individualNumber,
        "individualIssued" to Res.string.individualIssued,
        "individualDate" to Res.string.individualDate,

        "organizations" to Res.string.organizations,
        "organizationName" to Res.string.organizationName,
        "organizationConsumer" to Res.string.organizationConsumer,
        "organizationPayer" to Res.string.organizationPayer,
        "organizationAccount" to Res.string.organizationAccount,

        "products" to Res.string.products,
        "productName" to Res.string.productName,

        "suppliers" to Res.string.suppliers,
        "supplierName" to Res.string.supplierName,
    )

    fun get(name: String): StringResource = map[name] ?: error("Key $name not registered!")
}