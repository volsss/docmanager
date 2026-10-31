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

actual object DefaultConnectionConfig {
    actual val h2JdbcUrl = "jdbc:h2:/data/data/ru.example.docmanager/new_project;MODE=MySQL"
    actual val postgresJdbcUrl = "jdbc:postgresql://localhost:5432/new_project"
}