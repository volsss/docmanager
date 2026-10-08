/*
 * Copyright (c) 2026 Andrew Z.
 * docmanager — a scalable document management system.
 */

package ru.example.docmanager.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import ru.example.docmanager.database.Metadata
import ru.example.docmanager.database.repositories.MetadataRepository

class SettingsViewModel(
    private val metadataRepository: MetadataRepository
): ViewModel() {
    val metadata: StateFlow<Metadata?>
        field = MutableStateFlow<Metadata?>(null)

    init {
        loadMetadata()
    }

    private fun loadMetadata() {
        viewModelScope.launch {
            metadata.value = metadataRepository.getMetadata()
        }
    }

    fun updateMetadata(metadata: Metadata) {
        viewModelScope.launch {
            metadataRepository.updateMetadata(metadata)
            this@SettingsViewModel.metadata.value = metadata
        }
    }

    fun isMetadataLoaded() = metadata.value != null
}