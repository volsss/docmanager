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

package ru.example.docmanager.data.database.tables.references

import org.jetbrains.exposed.v1.core.dao.id.IntIdTable

object SuppliersTable : IntIdTable("suppliers") {
    val name = varchar("name", 256)
}