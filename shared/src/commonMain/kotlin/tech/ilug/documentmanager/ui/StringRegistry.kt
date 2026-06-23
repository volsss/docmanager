package tech.ilug.documentmanager.ui

import documentmanager.shared.generated.resources.Res
import documentmanager.shared.generated.resources.individualDate
import documentmanager.shared.generated.resources.individualIssued
import documentmanager.shared.generated.resources.individualJob
import documentmanager.shared.generated.resources.individualName
import documentmanager.shared.generated.resources.individualNumber
import documentmanager.shared.generated.resources.individualSeries
import documentmanager.shared.generated.resources.individuals
import documentmanager.shared.generated.resources.organizationAccount
import documentmanager.shared.generated.resources.organizationConsumer
import documentmanager.shared.generated.resources.organizationName
import documentmanager.shared.generated.resources.organizationPayer
import documentmanager.shared.generated.resources.organizations
import documentmanager.shared.generated.resources.productName
import documentmanager.shared.generated.resources.products
import documentmanager.shared.generated.resources.supplierName
import documentmanager.shared.generated.resources.suppliers
import org.jetbrains.compose.resources.StringResource

object StringRegistry {
    private val map = mapOf (
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

    fun get(name: String): StringResource {
        return map[name] ?: throw IllegalArgumentException("Key $name not registered!")
    }
}