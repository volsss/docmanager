package tech.ilug.documentmanager.model

import kotlinx.serialization.Serializable

@Serializable
data class ProjectMetadata(
    val projectName: String,
    val version: String,
    val creationDate: String,
    val author: String
)