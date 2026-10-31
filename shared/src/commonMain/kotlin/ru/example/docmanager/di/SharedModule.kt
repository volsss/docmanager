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

import org.koin.core.module.Module
import org.koin.dsl.module

val sharedModule = module {
    includes(
        dataModule,
        repositoryModule,
        viewModelModule,
        platformModule
    )
}

expect val platformModule: Module