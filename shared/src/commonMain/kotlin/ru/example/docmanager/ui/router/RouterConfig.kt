/*
 * Copyright (c) 2026 Andrew Z.
 * docmanager — a scalable document management system.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License v3.0.
 *
 * Distributed WITHOUT ANY WARRANTY. See the GNU General Public License
 * for more details: <https://gnu.org>
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
                Route.Form::class,
                Route.Form.serializer()
            )
            subclass(
                Route.Reference::class,
                Route.Reference.serializer()
            )
        }
    }
}

