package ru.example.docmanager.documents.base

import docmanager.shared.generated.resources.Res
import docmanager.shared.generated.resources.body_field_count
import docmanager.shared.generated.resources.body_field_number_sorted
import docmanager.shared.generated.resources.body_field_products
import docmanager.shared.generated.resources.body_field_unit
import org.jetbrains.compose.resources.StringResource

enum class BodyFieldType (
    val stringResource: StringResource
) {
    NUMBER_SORTED(Res.string.body_field_number_sorted),
    PRODUCTS(Res.string.body_field_products),
    UNIT(Res.string.body_field_unit),
    COUNT(Res.string.body_field_count),
}