package ru.example.docmanager.viewmodel

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.FindInPage
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import docmanager.shared.generated.resources.Res
import docmanager.shared.generated.resources.tabs_document
import docmanager.shared.generated.resources.tabs_documents
import docmanager.shared.generated.resources.tabs_reference
import docmanager.shared.generated.resources.tabs_references
import docmanager.shared.generated.resources.tabs_settings
import org.jetbrains.compose.resources.StringResource

enum class Tab (
    val stringResource: StringResource,
    val icon: ImageVector
) {
    DOCUMENTS(Res.string.tabs_documents, Icons.Default.FindInPage),
    REFERENCES(Res.string.tabs_references, Icons.Default.Book),
    SETTINGS(Res.string.tabs_settings, Icons.Default.Settings),
    DOCUMENT(Res.string.tabs_document, Icons.Default.FindInPage),
    REFERENCE(Res.string.tabs_reference, Icons.Default.Book)
}