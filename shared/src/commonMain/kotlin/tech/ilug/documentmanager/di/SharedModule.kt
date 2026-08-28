package tech.ilug.documentmanager.di

import org.koin.dsl.module
import tech.ilug.documentmanager.repository.documents.PowerOfAttorneyRepository
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
    /**
     * @param headReplacements Map of placeholder -> value
     * @param bodyParts Map of columnName -> List of values
     */
    fun processSave(headReplacements: Map<String, String>, bodyParts: Map<String, List<String>>)
    fun processPrint(headReplacements: Map<String, String>, bodyParts: Map<String, List<String>>)
}

expect fun getDocumentProcessor(): DocumentProcessor