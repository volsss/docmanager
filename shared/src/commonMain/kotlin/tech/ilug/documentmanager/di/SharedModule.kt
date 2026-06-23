package tech.ilug.documentmanager.di

import org.koin.dsl.module
import tech.ilug.documentmanager.repository.PowerOfAttorneyRepository
import tech.ilug.documentmanager.repository.ProjectMetadataRepository
import tech.ilug.documentmanager.repository.references.IndividualsRepository
import tech.ilug.documentmanager.repository.references.OrganizationsRepository
import tech.ilug.documentmanager.repository.references.ProductsRepository
import tech.ilug.documentmanager.repository.references.SuppliersRepository
import tech.ilug.documentmanager.viewmodel.ConnectionViewModel
import tech.ilug.documentmanager.viewmodel.DashboardViewModel
import tech.ilug.documentmanager.viewmodel.DocumentViewModel
import tech.ilug.documentmanager.viewmodel.ReferenceViewModel
import tech.ilug.documentmanager.viewmodel.SettingsViewModel

val sharedModule = module {
    single { ProjectMetadataRepository() }
    single { IndividualsRepository() }
    single { OrganizationsRepository() }
    single { ProductsRepository() }
    single { SuppliersRepository() }
    factory {
        PowerOfAttorneyRepository(
            get(),
            get(),
            get(),
            get()
        )
    }

    single {
        ReferenceViewModel(
            get(),
            get(),
            get(),
            get()
        )
    }
    single { ConnectionViewModel() }
    single { DashboardViewModel() }
    single { DocumentViewModel() }
    factory { SettingsViewModel(get()) }
}

enum class Platform {
    ANDROID, JVM
}

expect fun getPlatform(): Platform

interface DocumentProcessor {
    fun process(replacements: Map<String, String>)
}

expect fun getDocumentProcessor(): DocumentProcessor