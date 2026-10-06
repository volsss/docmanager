package ru.example.docmanager.documents.base

import docmanager.shared.generated.resources.Res
import docmanager.shared.generated.resources.document_power_of_attorney
import org.jetbrains.compose.resources.StringResource

enum class DocumentType (
    val stringResource: StringResource
) {
    POWER_OF_ATTORNEY (Res.string.document_power_of_attorney)
}