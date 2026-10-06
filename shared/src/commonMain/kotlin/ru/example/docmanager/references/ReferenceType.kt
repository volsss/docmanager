package ru.example.docmanager.references

import docmanager.shared.generated.resources.Res
import docmanager.shared.generated.resources.individuals
import docmanager.shared.generated.resources.organizations
import docmanager.shared.generated.resources.products
import docmanager.shared.generated.resources.suppliers
import org.jetbrains.compose.resources.StringResource

enum class ReferenceType (
    val stringResource: StringResource
) {
    INDIVIDUAL(Res.string.individuals),
    ORGANIZATION(Res.string.organizations),
    PRODUCT(Res.string.products),
    SUPPLIER(Res.string.suppliers)
}