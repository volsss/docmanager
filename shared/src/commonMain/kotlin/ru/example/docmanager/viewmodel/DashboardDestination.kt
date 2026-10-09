/*
 * Copyright (c) 2026 Andrew Z.
 * docmanager — a scalable document management system.
 */

package ru.example.docmanager.viewmodel

import androidx.navigation3.runtime.NavKey
import docmanager.shared.generated.resources.*
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.StringResource
import ru.example.docmanager.document.DocumentType
import ru.example.docmanager.reference.ReferenceType

@Serializable
sealed interface DashboardDestination : NavKey {
    @Contextual
    val stringResource: StringResource

    @Serializable
    data object Documents : DashboardDestination {
        @Contextual
        override val stringResource = Res.string.tabs_documents
    }

    @Serializable
    data object References : DashboardDestination {
        @Contextual
        override val stringResource = Res.string.tabs_references
    }
    @Serializable
    data object Settings : DashboardDestination {
        @Contextual
        override val stringResource = Res.string.tabs_settings
    }

    @Serializable
    data class Document(
        val type: DocumentType,
        @Contextual
        override val stringResource: StringResource = Res.string.tabs_document,
    ) : DashboardDestination

    @Serializable
    data class Reference(
        val type: ReferenceType,
        @Contextual
        override val stringResource: StringResource = Res.string.tabs_reference,
    ) : DashboardDestination
}