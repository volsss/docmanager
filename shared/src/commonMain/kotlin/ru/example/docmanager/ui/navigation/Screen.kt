/*
 * Copyright (c) 2026 Andrew Z.
 * docmanager — a scalable document management system.
 */

package ru.example.docmanager.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ViewList
import androidx.compose.material.icons.rounded.Description
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import ru.example.docmanager.document.DocumentType
import ru.example.docmanager.reference.ReferenceType

@Serializable
sealed interface Screen : NavKey {
    @Contextual
    val icon: ImageVector

    @Serializable
    data object Connection : Screen {
        @Contextual
        override val icon = Icons.Rounded.Person
    }

    @Serializable
    data object Start : Screen {
        @Contextual
        override val icon = Icons.Rounded.Person
    }

    @Serializable
    data object DocumentTypes : Screen {
        @Contextual
        override val icon = Icons.Rounded.Description
    }

    @Serializable
    data object References : Screen {
        @Contextual
        override val icon = Icons.AutoMirrored.Rounded.ViewList
    }

    @Serializable
    data object Settings : Screen {
        @Contextual
        override val icon = Icons.Rounded.Settings
    }

    @Serializable
    data class Documents (
        val type: DocumentType,
        @Contextual
        override val icon: ImageVector = Icons.Rounded.Description
    ) : Screen {
    }

    @Serializable
    data class Document(
        val type: DocumentType,
        @Contextual
        override val icon: ImageVector = Icons.Rounded.Person
    ) : Screen

    @Serializable
    data class Reference(
        val type: ReferenceType,
        @Contextual
        override val icon: ImageVector = Icons.Rounded.Person
    ) : Screen
}