package tech.ilug.documentmanager.database.models

import kotlinx.serialization.Serializable

@Serializable
data class Metadata(
    val name: String,
    val version: String,
    val creationDate: String,
    val author: String
)