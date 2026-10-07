/*
 * Copyright (c) 2026 Andrew Z.
 * docmanager — a scalable document management system.
 */

package ru.example.docmanager.document

import docmanager.shared.generated.resources.Res
import docmanager.shared.generated.resources.document_power_of_attorney
import org.jetbrains.compose.resources.StringResource

enum class DocumentType (
    val stringResource: StringResource
) {
    POWER_OF_ATTORNEY (Res.string.document_power_of_attorney)
}