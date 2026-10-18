/*
 * Copyright (c) 2026 Andrew Z.
 * docmanager — a scalable document management system.
 */

package ru.example.docmanager.di

import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.dsl.module
import ru.example.docmanager.data.database.DatabaseManager
import ru.example.docmanager.data.database.DefaultDatabaseManager
import ru.example.docmanager.data.repositories.MetadataRepository
import ru.example.docmanager.data.repositories.documents.BaseDocumentRepository
import ru.example.docmanager.data.repositories.documents.PowerOfAttorneyRepository
import ru.example.docmanager.data.repositories.references.*
import ru.example.docmanager.ui.viewmodel.PowerOfAttorneyViewModel
import ru.example.docmanager.ui.viewmodel.connection.ConnectionViewModel
import ru.example.docmanager.ui.viewmodel.documents.DocumentsViewModel
import ru.example.docmanager.ui.viewmodel.reference.ReferenceViewModel
import ru.example.docmanager.ui.viewmodel.settings.SettingsViewModel

val sharedModule = module {
    viewModelOf(::ConnectionViewModel)
    viewModelOf(::SettingsViewModel)
    single<DatabaseManager> { DefaultDatabaseManager() }
    singleOf(::MetadataRepository)

    // References
    singleOf(::IndividualsRepository) bind BaseReferenceRepository::class
    singleOf(::OrganizationsRepository) bind BaseReferenceRepository::class
    singleOf(::ProductsRepository) bind BaseReferenceRepository::class
    singleOf(::SuppliersRepository) bind BaseReferenceRepository::class
    single(named("references")) {
        getAll<BaseReferenceRepository>().associateBy { it.type }
    }

    viewModel { params ->
        ReferenceViewModel(
            repositories = get(named("references")),
            destination = params.get()
        )
    }

    // Documents
    singleOf(::PowerOfAttorneyRepository) bind BaseDocumentRepository::class
    single(named("documents")) {
        getAll<BaseDocumentRepository>().associateBy { it.type }
    }

    viewModelOf(::PowerOfAttorneyViewModel)

    viewModel { params ->
        DocumentsViewModel(
            repositories = get(named("documents")),
            destination = params.get()
        )
    }
}

expect fun getPlatform(): Platform
expect fun getDocumentProcessor(): DocumentProcessor

@Composable
expect fun getColorScheme(darkTheme: Boolean): ColorScheme