package ru.example.docmanager.di

import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import ru.example.docmanager.database.repositories.MetadataRepository
import ru.example.docmanager.database.repositories.documents.PowerOfAttorneyRepository
import ru.example.docmanager.database.repositories.references.IndividualsRepository
import ru.example.docmanager.database.repositories.references.OrganizationsRepository
import ru.example.docmanager.database.repositories.references.ProductsRepository
import ru.example.docmanager.database.repositories.references.SuppliersRepository
import ru.example.docmanager.viewmodel.ConnectionViewModel
import ru.example.docmanager.viewmodel.DashboardViewModel
import ru.example.docmanager.viewmodel.DocumentViewModel
import ru.example.docmanager.viewmodel.ReferenceViewModel
import ru.example.docmanager.viewmodel.SettingsViewModel

val sharedModule = module {
    singleOf(::MetadataRepository)
    singleOf(::IndividualsRepository)
    singleOf(::OrganizationsRepository)
    singleOf(::ProductsRepository)
    singleOf(::SuppliersRepository)
    factoryOf(::PowerOfAttorneyRepository)

    singleOf(::ReferenceViewModel)
    singleOf(::ConnectionViewModel)
    singleOf(::DashboardViewModel)
    singleOf(::DocumentViewModel)
    factoryOf(::SettingsViewModel)
}

enum class Platform {
    ANDROID, JVM
}

expect fun getPlatform(): Platform
expect fun getDocumentProcessor(): DocumentProcessor