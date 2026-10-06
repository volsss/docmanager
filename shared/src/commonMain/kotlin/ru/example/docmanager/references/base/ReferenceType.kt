package ru.example.docmanager.references.base

import docmanager.shared.generated.resources.Res
import docmanager.shared.generated.resources.reference_individuals
import docmanager.shared.generated.resources.reference_organizations
import docmanager.shared.generated.resources.reference_products
import docmanager.shared.generated.resources.reference_suppliers
import org.jetbrains.compose.resources.StringResource

enum class ReferenceType (
    val stringResource: StringResource
) {
    INDIVIDUAL(Res.string.reference_individuals),
    ORGANIZATION(Res.string.reference_organizations),
    PRODUCT(Res.string.reference_products),
    SUPPLIER(Res.string.reference_suppliers)
}