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

package ru.example.docmanager.di

import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.core.qualifier.named
import org.koin.dsl.module
import ru.example.docmanager.ui.viewmodel.connection.ConnectionViewModel
import ru.example.docmanager.ui.viewmodel.documents.DocumentsViewModel
import ru.example.docmanager.ui.viewmodel.forms.powerofattorney.PowerOfAttorneyViewModel
import ru.example.docmanager.ui.viewmodel.reference.ReferenceViewModel
import ru.example.docmanager.ui.viewmodel.settings.SettingsViewModel
import ru.example.docmanager.ui.viewmodel.start.StartViewModel

val viewModelModule = module {
    viewModelOf(::ConnectionViewModel)
    viewModelOf(::SettingsViewModel)
    viewModelOf(::PowerOfAttorneyViewModel)

    viewModel {
        StartViewModel(
            metadataRepository = get(),
            databaseManager = get(),
            documentRepositories = get(named("documents")),
            referenceRepositories = get(named("references"))
        )
    }

    viewModel { params ->
        ReferenceViewModel(
            repositories = get(named("references")),
            destination = params.get()
        )
    }

    viewModel { params ->
        DocumentsViewModel(
            repositories = get(named("documents")),
            destination = params.get()
        )
    }
}