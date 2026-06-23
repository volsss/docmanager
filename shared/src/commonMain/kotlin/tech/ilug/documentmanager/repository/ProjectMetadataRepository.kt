package tech.ilug.documentmanager.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.jetbrains.exposed.v1.jdbc.update
import tech.ilug.documentmanager.database.ProjectMetadataTable
import tech.ilug.documentmanager.model.ProjectMetadata

class ProjectMetadataRepository {
    suspend fun getMetadata(): ProjectMetadata = withContext(Dispatchers.IO) {
        transaction {
            val row = ProjectMetadataTable.selectAll().first()
            ProjectMetadata(
                projectName = row[ProjectMetadataTable.projectName],
                version = row[ProjectMetadataTable.version],
                creationDate = row[ProjectMetadataTable.creationDate],
                author = row[ProjectMetadataTable.author]
            )
        }
    }

    suspend fun updateMetadata(metadata: ProjectMetadata) {
        withContext(Dispatchers.IO) {
            transaction {
                ProjectMetadataTable.update {
                    it[projectName] = metadata.projectName
                    it[version] = metadata.version
                    it[creationDate] = metadata.creationDate
                    it[author] = metadata.author
                }
            }
        }
    }
}