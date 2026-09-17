package tech.ilug.documentmanager.database.repositories

import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.update
import tech.ilug.documentmanager.database.tables.MetadataTable
import tech.ilug.documentmanager.database.dbQuery
import tech.ilug.documentmanager.database.models.Metadata

class MetadataRepository {
    suspend fun getMetadata(): Metadata = dbQuery {
        val row = MetadataTable.selectAll().first()
        Metadata(
            name = row[MetadataTable.name],
            version = row[MetadataTable.version],
            creationDate = row[MetadataTable.creationDate],
            author = row[MetadataTable.author]
        )
    }

    suspend fun updateMetadata(metadata: Metadata): Unit = dbQuery {
        MetadataTable.update {
            it[name] = metadata.name
            it[version] = metadata.version
            it[creationDate] = metadata.creationDate
            it[author] = metadata.author
        }
    }
}