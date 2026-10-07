/*
 * Copyright (c) 2026 Andrew Z.
 * docmanager — a scalable document management system.
 */

package ru.example.docmanager.viewmodel

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.FindInPage
import docmanager.shared.generated.resources.Res
import docmanager.shared.generated.resources.tabs_documents
import docmanager.shared.generated.resources.tabs_references
import docmanager.shared.generated.resources.tabs_settings
import ru.example.docmanager.document.DocumentType
import ru.example.docmanager.reference.ReferenceType

sealed interface DashboardDestination {
    data object Documents : DashboardDestination {
        val stringResource = Res.string.tabs_documents
        val icon = Icons.Default.FindInPage
    }
    data object References : DashboardDestination {
        val stringResource = Res.string.tabs_references
        val icon = Icons.Default.Book
    }
    data object Settings : DashboardDestination {
        val stringResource = Res.string.tabs_settings
    }

    data class Document(
        val type: DocumentType,
    ) : DashboardDestination

    data class Reference(
        val type: ReferenceType,
    ) : DashboardDestination
}