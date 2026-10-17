/*
 * Copyright (c) 2026 Andrew Z.
 * docmanager — a scalable document management system.
 */

package ru.example.docmanager.ui.router

import androidx.navigation3.runtime.NavKey
import androidx.savedstate.serialization.SavedStateConfiguration
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic

val routerConfig = SavedStateConfiguration {
    serializersModule = SerializersModule {
        polymorphic(NavKey::class) {
            subclass(
                Route.Start::class,
                Route.Start.serializer()
            )
            subclass(
                Route.Documents::class,
                Route.Documents.serializer()
            )
            subclass(
                Route.References::class,
                Route.References.serializer()
            )
            subclass(
                Route.Settings::class,
                Route.Settings.serializer()
            )
            subclass(
                Route.Document::class,
                Route.Document.serializer()
            )
            subclass(
                Route.Reference::class,
                Route.Reference.serializer()
            )
        }
    }
}

