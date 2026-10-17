/*
 * Copyright (c) 2026 Andrew Z.
 * docmanager — a scalable document management system.
 */

package ru.example.docmanager.ui.viewmodel.settings

data class SettingsUiState(
    val projectName: String = "",
    val version: String = "",
    val author: String = "",
    val creationDate: String = ""
)
