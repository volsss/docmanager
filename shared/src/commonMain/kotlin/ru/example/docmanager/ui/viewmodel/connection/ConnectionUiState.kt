/*
 * Copyright (c) 2026 Andrew Z.
 * docmanager — a scalable document management system.
 */

package ru.example.docmanager.ui.viewmodel.connection

data class ConnectionUiState(
    val jdbcUrl: String = "",
    val driver: String = "",
    val username: String = "",
    val password: String = "",

    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isConnected: Boolean = false
)
