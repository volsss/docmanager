/*
 * Copyright (c) 2026 Andrew Z.
 * docmanager — a scalable document management system.
 */

package ru.example.docmanager.data.database.tables.references

import org.jetbrains.exposed.v1.core.dao.id.IntIdTable

object ProductsTable : IntIdTable("products") {
    val name = varchar("name", 256)
}