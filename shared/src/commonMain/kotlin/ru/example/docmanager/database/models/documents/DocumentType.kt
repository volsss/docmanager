package ru.example.docmanager.database.models.documents

import docmanager.shared.generated.resources.Res
import docmanager.shared.generated.resources.power_of_attorney
import org.jetbrains.compose.resources.StringResource

enum class DocumentType (
    val stringResource: StringResource
) {
    POWER_OF_ATTORNEY (Res.string.power_of_attorney)
}