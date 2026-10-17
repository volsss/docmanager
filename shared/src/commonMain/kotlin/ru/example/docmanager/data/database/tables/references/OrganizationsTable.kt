/*
 * Copyright (c) 2026 Andrew Z.
 * docmanager — a scalable document management system.
 */

package ru.example.docmanager.data.database.tables.references

import org.jetbrains.exposed.v1.core.dao.id.IntIdTable

object OrganizationsTable : IntIdTable("organizations") {
    val name = varchar("name", 256)
    val consumer = varchar("consumer", 256)
    val payer = varchar("payer", 256)
    val account = varchar("account", 256)
}