package tech.ilug.documentmanager.di

import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import tech.ilug.documentmanager.database.repositories.MetadataRepository
import tech.ilug.documentmanager.database.repositories.documents.PowerOfAttorneyRepository
import tech.ilug.documentmanager.database.repositories.references.IndividualsRepository
import tech.ilug.documentmanager.database.repositories.references.OrganizationsRepository
import tech.ilug.documentmanager.database.repositories.references.ProductsRepository
import tech.ilug.documentmanager.database.repositories.references.SuppliersRepository
import tech.ilug.documentmanager.viewmodel.ConnectionViewModel
import tech.ilug.documentmanager.viewmodel.DashboardViewModel
import tech.ilug.documentmanager.viewmodel.DocumentViewModel
import tech.ilug.documentmanager.viewmodel.ReferenceViewModel
import tech.ilug.documentmanager.viewmodel.SettingsViewModel

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