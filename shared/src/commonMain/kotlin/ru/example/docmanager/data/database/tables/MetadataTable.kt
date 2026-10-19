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

package ru.example.docmanager.data.database.tables

import org.jetbrains.exposed.v1.core.Table
import org.jetbrains.exposed.v1.javatime.date

object MetadataTable : Table("metadata") {
    val id = integer("id").autoIncrement()
    val name = varchar("name", 255)
    val version = varchar("version", 50)
    val creationDate = date("creation_date")
    val author = varchar("author", 255)
    override val primaryKey = PrimaryKey(id)
}