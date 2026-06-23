package tech.ilug.documentmanager.viewmodel

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import tech.ilug.documentmanager.model.ProjectMetadata
import tech.ilug.documentmanager.repository.ProjectMetadataRepository

class SettingsViewModel(
    private val metadataRepository: ProjectMetadataRepository
) {
    val metadata: StateFlow<ProjectMetadata?>
        field = MutableStateFlow<ProjectMetadata?>(null)

    suspend fun loadMetadata() {
        metadata.value = metadataRepository.getMetadata()
    }

    suspend fun updateMetadata(metadata: ProjectMetadata) {
        metadataRepository.updateMetadata(metadata)
        this.metadata.value = metadata
    }

    fun isMetadataLoaded() = metadata.value != null
}