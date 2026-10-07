/*
 * Copyright (c) 2026 Andrew Z.
 * docmanager — a scalable document management system.
 */

package ru.example.docmanager.document.body

import docmanager.shared.generated.resources.*
import org.jetbrains.compose.resources.StringResource

enum class BodyFieldType (
    val stringResource: StringResource
) {
    NUMBER_SORTED(Res.string.body_field_number_sorted),
    PRODUCTS(Res.string.body_field_products),
    UNIT(Res.string.body_field_unit),
    COUNT(Res.string.body_field_count),
}