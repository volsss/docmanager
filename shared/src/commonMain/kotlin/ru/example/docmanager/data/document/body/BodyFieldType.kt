/*
 * Copyright (c) 2026 Andrew Z.
 * docmanager — a scalable document management system.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License v3.0.
 *
 * Distributed WITHOUT ANY WARRANTY. See the GNU General Public License
 * for more details: <https://gnu.org>
 */

package ru.example.docmanager.data.document.body

import docmanager.shared.generated.resources.*
import org.jetbrains.compose.resources.StringResource

enum class BodyFieldType(
    val stringResource: StringResource
) {
    NUMBER_SORTED(Res.string.body_field_number_sorted),
    PRODUCTS(Res.string.body_field_products),
    UNIT(Res.string.body_field_unit),
    COUNT(Res.string.body_field_count),
}