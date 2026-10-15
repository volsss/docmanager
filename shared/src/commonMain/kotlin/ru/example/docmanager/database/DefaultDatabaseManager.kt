/*
 * Copyright (c) 2026 Andrew Z.
 * docmanager — a scalable document management system.
 */

package ru.example.docmanager.database

import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.SchemaUtils
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.TransactionManager
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import ru.example.docmanager.database.tables.MetadataTable
import ru.example.docmanager.database.tables.documents.powerofattorney.PowerOfAttorneyBodiesTable
import ru.example.docmanager.database.tables.documents.powerofattorney.PowerOfAttorneyHeadersTable
import ru.example.docmanager.database.tables.references.IndividualsTable
import ru.example.docmanager.database.tables.references.OrganizationsTable
import ru.example.docmanager.database.tables.references.ProductsTable
import ru.example.docmanager.database.tables.references.SuppliersTable
import kotlin.time.Clock

class DefaultDatabaseManager : DatabaseManager {
    private var db: Database? = null
    private var initialized = false

    override fun init(jdbcUrl: String, driver: String, user: String?, password: String?) {
        if (this.initialized)
            throw IllegalStateException("Database already initialized")

        this.db = Database.connect(
            url = jdbcUrl,
            driver = driver,
            user = user ?: "",
            password = password ?: ""
        )

        transaction {
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
        this.db = null
        this.initialized = false
    }

    override fun isInitialized(): Boolean = this.initialized
}