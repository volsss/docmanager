/*
 * Copyright (c) 2026 Andrew Z.
 * docmanager — a scalable document management system.
 */

package ru.example.docmanager.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import ru.example.docmanager.database.Metadata
import ru.example.docmanager.database.repositories.MetadataRepository

class SettingsViewModel(
    private val metadataRepository: MetadataRepository
): ViewModel() {
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