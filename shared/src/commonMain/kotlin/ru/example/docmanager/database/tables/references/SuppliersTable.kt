/*
 * Copyright (c) 2026 Andrew Z.
 * docmanager — a scalable document management system.
 */

package ru.example.docmanager.database.tables.references

import org.jetbrains.exposed.v1.core.dao.id.IntIdTable

object SuppliersTable : IntIdTable("suppliers") {
    val name = varchar("name", 256)
}