/*
 * Copyright (c) 2026 Andrew Z.
 * docmanager — a scalable document management system.
 */

package ru.example.docmanager.database.repositories

import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.update
import ru.example.docmanager.database.tables.MetadataTable
import ru.example.docmanager.database.dbQuery
import ru.example.docmanager.database.Metadata

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