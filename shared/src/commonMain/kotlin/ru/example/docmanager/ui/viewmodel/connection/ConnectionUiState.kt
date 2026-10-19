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
