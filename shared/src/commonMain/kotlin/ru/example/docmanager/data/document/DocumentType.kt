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

package ru.example.docmanager.data.document

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Description
import androidx.compose.material.icons.rounded.TableView
import androidx.compose.ui.graphics.vector.ImageVector
import docmanager.shared.generated.resources.Res
import docmanager.shared.generated.resources.document_power_of_attorney
import docmanager.shared.generated.resources.document_test
import org.jetbrains.compose.resources.StringResource

enum class DocumentType(
    val stringResource: StringResource,
    val icon: ImageVector
) {
    POWER_OF_ATTORNEY(
        stringResource = Res.string.document_power_of_attorney,
        icon = Icons.Rounded.TableView
    ),
    TEST(
        stringResource = Res.string.document_test,
        icon = Icons.Rounded.Description
    )
}