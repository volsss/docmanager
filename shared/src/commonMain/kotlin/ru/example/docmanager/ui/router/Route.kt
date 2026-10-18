/*
 * Copyright (c) 2026 Andrew Z.
 * docmanager — a scalable document management system.
 */

package ru.example.docmanager.ui.router

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ViewList
import androidx.compose.material.icons.rounded.Description
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation3.runtime.NavKey
import docmanager.shared.generated.resources.*
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.StringResource
import ru.example.docmanager.data.document.Document
import ru.example.docmanager.data.document.DocumentType
import ru.example.docmanager.data.reference.ReferenceType

@Serializable
sealed interface Route : NavKey {
    @Contextual
    val stringResource: StringResource

    @Contextual
    val icon: ImageVector

    @Serializable
    data object Start : Route {
        @Contextual
        override val stringResource = Res.string.route_home

        @Contextual
        override val icon = Icons.Rounded.Home
    }

    @Serializable
    data object DocumentTypes : Route {
        @Contextual
        override val stringResource = Res.string.route_document_types

        @Contextual
        override val icon = Icons.Rounded.Description
    }

    @Serializable
    data object References : Route {
        @Contextual
        override val stringResource = Res.string.route_references

        @Contextual
        override val icon = Icons.AutoMirrored.Rounded.ViewList
    }

    @Serializable
    data object Settings : Route {
        @Contextual
        override val stringResource = Res.string.route_settings

        @Contextual
        override val icon = Icons.Rounded.Settings
    }

    @Serializable
    data class Documents(
        val type: DocumentType,
        @Contextual
        override val stringResource: StringResource = Res.string.route_documents,
        @Contextual
        override val icon: ImageVector = Icons.Rounded.Description
    ) : Route

    @Serializable
    data class Form(
        val type: DocumentType,
        val document: Document? = null,
        @Contextual
        override val stringResource: StringResource = Res.string.route_form,
        @Contextual
        override val icon: ImageVector = Icons.Rounded.Person
    ) : Route

    @Serializable
    data class Reference(
        val type: ReferenceType,
        @Contextual
        override val stringResource: StringResource = Res.string.route_reference,
        @Contextual
        override val icon: ImageVector = Icons.Rounded.Person
    ) : Route
}