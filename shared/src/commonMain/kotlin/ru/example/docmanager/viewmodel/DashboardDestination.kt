/*
 * Copyright (c) 2026 Andrew Z.
 * docmanager — a scalable document management system.
 */

package ru.example.docmanager.viewmodel

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable
import ru.example.docmanager.document.DocumentType
import ru.example.docmanager.reference.ReferenceType

@Serializable
sealed interface DashboardDestination : NavKey {
    @Serializable data object Documents : DashboardDestination
    @Serializable data object References : DashboardDestination
    @Serializable data object Settings : DashboardDestination

    @Serializable
    data class Document(val type: DocumentType) : DashboardDestination

    @Serializable
    data class Reference(val type: ReferenceType) : DashboardDestination
}