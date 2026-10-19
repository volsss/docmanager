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

package ru.example.docmanager.ui.viewmodel.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import ru.example.docmanager.data.Metadata
import ru.example.docmanager.data.repositories.MetadataRepository

class SettingsViewModel(
    private val metadataRepository: MetadataRepository
) : ViewModel() {
    val uiState: StateFlow<SettingsUiState>
        field = MutableStateFlow(SettingsUiState())

    init {
        loadMetadata()
    }

    fun onProjectNameChanged(projectName: String) {
        uiState.value = uiState.value.copy(projectName = projectName)
    }

    fun onVersionChanged(version: String) {
        uiState.value = uiState.value.copy(version = version)
    }

    fun onAuthorChanged(author: String) {
        uiState.value = uiState.value.copy(author = author)
    }

    private fun loadMetadata() {
        viewModelScope.launch {
            runCatching {
                metadataRepository.getMetadata()
            }.onSuccess {
                uiState.value = SettingsUiState(
                    projectName = it.name,
                    version = it.version,
                    author = it.author,
                    creationDate = it.creationDate
                )
            }
        }
    }

    fun save() {
        viewModelScope.launch {
            metadataRepository.updateMetadata(
                Metadata(
                    name = uiState.value.projectName,
                    version = uiState.value.version,
                    author = uiState.value.author,
                    creationDate = uiState.value.creationDate
                )
            )
        }
    }
}