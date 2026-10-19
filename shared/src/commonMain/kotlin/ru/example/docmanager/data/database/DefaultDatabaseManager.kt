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

import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.SchemaUtils
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.TransactionManager
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import ru.example.docmanager.data.database.tables.MetadataTable
import ru.example.docmanager.data.database.tables.documents.powerofattorney.PowerOfAttorneyBodiesTable
import ru.example.docmanager.data.database.tables.documents.powerofattorney.PowerOfAttorneyHeadersTable
import ru.example.docmanager.data.database.tables.references.IndividualsTable
import ru.example.docmanager.data.database.tables.references.OrganizationsTable
import ru.example.docmanager.data.database.tables.references.ProductsTable
import ru.example.docmanager.data.database.tables.references.SuppliersTable
import kotlin.time.Clock

class DefaultDatabaseManager : DatabaseManager {
    private var db: Database? = null
    private var initialized = false

    override fun init(jdbcUrl: String, driver: String, user: String?, password: String?) {
        if (this.initialized)
            throw IllegalStateException("Database already initialized")

        val database = Database.connect(
            url = jdbcUrl,
            driver = driver,
            user = user ?: "",
            password = password ?: ""
        )
        this.db = database
        TransactionManager.defaultDatabase = database

        transaction(database) {
            SchemaUtils.create(
                MetadataTable,
                ProductsTable,
                SuppliersTable,
                IndividualsTable,
                OrganizationsTable,
                PowerOfAttorneyBodiesTable,
                PowerOfAttorneyHeadersTable,
            )

            if (MetadataTable.selectAll().empty()) {
                MetadataTable.insert {
                    it[name] = "Новый проект"
                    it[version] = "1.0"
                    it[creationDate] = Clock.System.todayIn(TimeZone.currentSystemDefault()).toString()
                    it[author] = "Пользователь"
                }
            }
        }

        this.initialized = true
    }

    override fun disconnect() {
        this.db?.let {
            TransactionManager.closeAndUnregister(it)
        }
        if (TransactionManager.defaultDatabase == this.db) {
            TransactionManager.defaultDatabase = null
        }
        this.db = null
        this.initialized = false
    }

    override fun isInitialized(): Boolean = this.initialized
}