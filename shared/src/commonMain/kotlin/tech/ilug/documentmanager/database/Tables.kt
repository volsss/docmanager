package tech.ilug.documentmanager.database

import org.jetbrains.exposed.v1.core.Table

object ProjectMetadataTable : Table("project_metadata") {
    val id = integer("id").autoIncrement()
    val projectName = varchar("project_name", 255)
    val version = varchar("version", 50)
    val creationDate = varchar("creation_date", 50)
    val author = varchar("author", 255)
    override val primaryKey = PrimaryKey(id)
}