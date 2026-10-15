/*
 * Copyright (c) 2026 Andrew Z.
 * docmanager — a scalable document management system.
 */

package ru.example.docmanager.ui.navigation

import androidx.navigation3.runtime.NavKey
import androidx.savedstate.serialization.SavedStateConfiguration
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic

val navigationConfig = SavedStateConfiguration {
    serializersModule = SerializersModule {
        polymorphic(NavKey::class) {
            subclass(
                Screen.Connection::class,
                Screen.Connection.serializer()
            )
            subclass(
                Screen.Start::class,
                Screen.Start.serializer()
            )
            subclass(
                Screen.Documents::class,
                Screen.Documents.serializer()
            )
            subclass(
                Screen.References::class,
                Screen.References.serializer()
            )
            subclass(
                Screen.Settings::class,
                Screen.Settings.serializer()
            )
            subclass(
                Screen.Document::class,
                Screen.Document.serializer()
            )
            subclass(
                Screen.Reference::class,
                Screen.Reference.serializer()
            )
        }
    }
}

