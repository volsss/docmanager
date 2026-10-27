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

package ru.example.docmanager.data.database

enum class DatabaseDriver(
    val displayName: String,
    val driverClassName: String
) {
    H2(
        displayName = "H2 Database",
        driverClassName = "org.h2.Driver"
    ),
    POSTGRESQL(
        displayName = "PostgreSQL",
        driverClassName = "org.postgresql.Driver"
    );

    companion object {
        fun fromDriverClassName(driverClassName: String): DatabaseDriver? {
            return entries.firstOrNull { it.driverClassName.equals(driverClassName, ignoreCase = true) }
        }
    }
}
