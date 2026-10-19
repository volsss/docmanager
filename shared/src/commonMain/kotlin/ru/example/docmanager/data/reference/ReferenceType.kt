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

package ru.example.docmanager.data.reference

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Business
import androidx.compose.material.icons.rounded.Face
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.ShoppingCart
import androidx.compose.ui.graphics.vector.ImageVector
import docmanager.shared.generated.resources.*
import org.jetbrains.compose.resources.StringResource

enum class ReferenceType(
    val stringResource: StringResource,
    val icon: ImageVector
) {
    INDIVIDUAL(
        stringResource = Res.string.reference_individuals,
        icon = Icons.Rounded.Face
    ),
    ORGANIZATION(
        stringResource = Res.string.reference_organizations,
        icon = Icons.Rounded.Business
    ),
    PRODUCT(
        stringResource = Res.string.reference_products,
        icon = Icons.Rounded.ShoppingCart
    ),
    SUPPLIER(
        stringResource = Res.string.reference_suppliers,
        icon = Icons.Rounded.Person
    )
}