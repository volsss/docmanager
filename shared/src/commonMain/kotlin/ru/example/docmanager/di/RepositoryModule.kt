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

import org.koin.core.module.dsl.singleOf
import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.dsl.module
import ru.example.docmanager.data.repositories.MetadataRepository
import ru.example.docmanager.data.repositories.documents.BaseDocumentRepository
import ru.example.docmanager.data.repositories.documents.PowerOfAttorneyRepository
import ru.example.docmanager.data.repositories.references.*

val repositoryModule = module {
    singleOf(::MetadataRepository)

    singleOf(::IndividualsRepository) bind BaseReferenceRepository::class
    singleOf(::OrganizationsRepository) bind BaseReferenceRepository::class
    singleOf(::ProductsRepository) bind BaseReferenceRepository::class
    singleOf(::SuppliersRepository) bind BaseReferenceRepository::class

    single(named("references")) {
        getAll<BaseReferenceRepository>().associateBy { it.type }
    }

    singleOf(::PowerOfAttorneyRepository) bind BaseDocumentRepository::class

    single(named("documents")) {
        getAll<BaseDocumentRepository>().associateBy { it.type }
    }
}