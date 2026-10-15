/*
 * Copyright (c) 2026 Andrew Z.
 * docmanager — a scalable document management system.
 */

package ru.example.docmanager.di

import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import ru.example.docmanager.database.DatabaseManager
import ru.example.docmanager.database.DefaultDatabaseManager
import ru.example.docmanager.database.repositories.MetadataRepository
import ru.example.docmanager.database.repositories.documents.DocumentRepository
import ru.example.docmanager.database.repositories.documents.PowerOfAttorneyRepository
import ru.example.docmanager.database.repositories.references.*
import ru.example.docmanager.document.type.powerofattorney.PowerOfAttorney
import ru.example.docmanager.reference.Individual
import ru.example.docmanager.reference.Organization
import ru.example.docmanager.reference.Product
import ru.example.docmanager.reference.Supplier
import ru.example.docmanager.viewmodel.*

val sharedModule = module {
    single<DatabaseManager> { DefaultDatabaseManager() }
    singleOf(::MetadataRepository)
    single<ReferenceRepository<Individual>> { IndividualsRepository() }
    single<ReferenceRepository<Organization>> { OrganizationsRepository() }
    single<ReferenceRepository<Product>> { ProductsRepository() }
    single<ReferenceRepository<Supplier>> { SuppliersRepository() }
    singleOf(::IndividualsRepository)
    singleOf(::OrganizationsRepository)
    singleOf(::ProductsRepository)
    singleOf(::SuppliersRepository)

    factory<DocumentRepository<PowerOfAttorney>> { PowerOfAttorneyRepository() }
    factoryOf(::PowerOfAttorneyRepository)

    singleOf(::DocumentsViewModel)
    singleOf(::ReferenceViewModel)
    singleOf(::AppViewModel)
    factoryOf(::PowerOfAttorneyViewModel)
    factoryOf(::SettingsViewModel)
}

enum class Platform {
    ANDROID, JVM
}

expect fun getPlatform(): Platform
expect fun getDocumentProcessor(): DocumentProcessor

@Composable
expect fun getColorScheme(darkTheme: Boolean): ColorScheme