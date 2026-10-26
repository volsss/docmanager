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

package ru.example.docmanager.ui.utils

import ru.example.docmanager.data.database.DatabaseDriver

object Utils {
    const val JVM_H2_JDBC = "jdbc:h2:~/ru.example.docmanager/new_project;MODE=MySQL"
    const val ANDROID_H2_JDBC = "jdbc:h2:/data/data/ru.example.docmanager/new_project;MODE=MySQL"
    const val DEFAULT_POSTGRES_JDBC = "jdbc:postgresql://localhost:5432/new_project"

    val H2_DRIVER = DatabaseDriver.H2.driverClassName
    val POSTGRES_DRIVER = DatabaseDriver.POSTGRESQL.driverClassName
}