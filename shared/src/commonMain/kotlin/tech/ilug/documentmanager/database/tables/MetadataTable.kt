package tech.ilug.documentmanager.database.tables

import org.jetbrains.exposed.v1.core.Table

object MetadataTable : Table("metadata") {
    val id = integer("id").autoIncrement()
    val name = varchar("name", 255)
    val version = varchar("version", 50)
    val creationDate = varchar("creation_date", 50)
    val author = varchar("author", 255)
    override val primaryKey = PrimaryKey(id)
}