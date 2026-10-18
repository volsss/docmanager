/*
 * Copyright (c) 2026 Andrew Z.
 * docmanager — a scalable document management system.
 */

package ru.example.docmanager.di

import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module
import ru.example.docmanager.data.database.DatabaseManager
import ru.example.docmanager.data.database.DefaultDatabaseManager
import ru.example.docmanager.data.document.DocumentType
import ru.example.docmanager.data.repositories.MetadataRepository
import ru.example.docmanager.data.repositories.documents.BaseDocumentRepository
import ru.example.docmanager.data.repositories.documents.PowerOfAttorneyRepository
import ru.example.docmanager.data.repositories.references.IndividualsRepository
import ru.example.docmanager.data.repositories.references.OrganizationsRepository
import ru.example.docmanager.data.repositories.references.ProductsRepository
import ru.example.docmanager.data.repositories.references.SuppliersRepository
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
    singleOf(::IndividualsRepository)
    singleOf(::OrganizationsRepository)
    singleOf(::ProductsRepository)
    singleOf(::SuppliersRepository)

    singleOf(::PowerOfAttorneyRepository) bind BaseDocumentRepository::class

    single<Map<DocumentType, BaseDocumentRepository>> {
        getAll<BaseDocumentRepository>().associateBy { it.type }
    }

    viewModel { params ->
        DocumentsViewModel(
            get(),
            destination = params.get()
        )
    }

    // TODO:
    singleOf(::ReferenceViewModel)
    factoryOf(::PowerOfAttorneyViewModel)
}

expect fun getPlatform(): Platform
expect fun getDocumentProcessor(): DocumentProcessor

@Composable
expect fun getColorScheme(darkTheme: Boolean): ColorScheme