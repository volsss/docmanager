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

package ru.example.docmanager.data.repositories

import kotlinx.datetime.toJavaLocalDate
import kotlinx.datetime.toKotlinLocalDate
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.update
import ru.example.docmanager.data.Metadata
import ru.example.docmanager.data.database.dbQuery
import ru.example.docmanager.data.database.tables.MetadataTable

class MetadataRepository {
    suspend fun getMetadata(): Metadata = dbQuery {
        val row = MetadataTable.selectAll().first()
        Metadata(
            name = row[MetadataTable.name],
            version = row[MetadataTable.version],
            creationDate = row[MetadataTable.creationDate].toKotlinLocalDate(),
            author = row[MetadataTable.author]
        )
    }

    suspend fun updateMetadata(metadata: Metadata): Unit = dbQuery {
        MetadataTable.update {
            it[name] = metadata.name
            it[version] = metadata.version
            it[creationDate] = metadata.creationDate.toJavaLocalDate()
            it[author] = metadata.author
        }
    }
}