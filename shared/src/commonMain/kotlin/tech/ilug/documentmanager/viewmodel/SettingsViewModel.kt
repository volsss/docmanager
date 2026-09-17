package tech.ilug.documentmanager.viewmodel

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import tech.ilug.documentmanager.database.models.Metadata
import tech.ilug.documentmanager.database.repositories.MetadataRepository

class SettingsViewModel(
    private val metadataRepository: MetadataRepository
) {
    val metadata: StateFlow<Metadata?>
        field = MutableStateFlow<Metadata?>(null)

    suspend fun loadMetadata() {
        metadata.value = metadataRepository.getMetadata()
    }

    suspend fun updateMetadata(metadata: Metadata) {
        metadataRepository.updateMetadata(metadata)
        this.metadata.value = metadata
    }

    fun isMetadataLoaded() = metadata.value != null
}