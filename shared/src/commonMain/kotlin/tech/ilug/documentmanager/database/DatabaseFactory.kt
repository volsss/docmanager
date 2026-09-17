package tech.ilug.documentmanager.database

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.exposed.v1.core.Transaction
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.SchemaUtils
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.TransactionManager
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import tech.ilug.documentmanager.database.tables.PowerOfAttorneyTables
import tech.ilug.documentmanager.database.tables.MetadataTable
import java.time.LocalDate

object DatabaseFactory {
    private var db: Database? = null
    private var initialized = false

    fun init(jdbcUrl: String, driver: String, user: String?, password: String?) {
        if (isInitialized()) throw IllegalStateException("Database already initialized")

        db = if (user != null && password != null) {
            Database.connect(jdbcUrl, driver = driver, user = user, password = password)
        } else {
            Database.connect(jdbcUrl, driver = driver)
        }
        transaction {
            SchemaUtils.create(
                MetadataTable,
                PowerOfAttorneyTables.ProductsTable,
                PowerOfAttorneyTables.SuppliersTable,
                PowerOfAttorneyTables.IndividualsTable,
                PowerOfAttorneyTables.OrganizationsTable,
                PowerOfAttorneyTables.BodiesTable,
                PowerOfAttorneyTables.HeadersTable,
            )
            if (MetadataTable.selectAll().empty()) {
                MetadataTable.insert {
                    it[name] = "Новый проект"
                    it[version] = "1.0"
                    it[creationDate] = LocalDate.now().toString()
                    it[author] = "Пользователь"
                }
            }
        }
        initialized = true
    }

    fun disconnect() {
        db?.let { TransactionManager.closeAndUnregister(it) }
        initialized = false
    }

    fun isInitialized(): Boolean = initialized
}

suspend fun <T> dbQuery(block: Transaction.() -> T): T = withContext(Dispatchers.IO) {
    transaction { block() }
}