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

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.example.docmanager.data.database.DatabaseDriver
import ru.example.docmanager.data.database.DatabaseManager
import ru.example.docmanager.data.database.DefaultConnectionConfig

class ConnectionViewModel(
    private val databaseManager: DatabaseManager
) : ViewModel() {
    companion object {
        fun formatPostgresJdbcUrl(ip: String): String {
            val trimmed = ip.trim()
            if (trimmed.isEmpty()) return ""
            if (trimmed.startsWith("jdbc:postgresql://")) return trimmed
            return if (trimmed.contains(':') || trimmed.contains('/')) {
                if (trimmed.contains('/')) {
                    "jdbc:postgresql://$trimmed"
                } else {
                    "jdbc:postgresql://$trimmed/new_project"
                }
            } else {
                "jdbc:postgresql://$trimmed:5432/new_project"
            }
        }
    }

    val uiState: StateFlow<ConnectionUiState>
        field = MutableStateFlow(
            ConnectionUiState(
                connectionType = ConnectionType.LOCAL,
                driver = DatabaseDriver.H2.driverClassName,
                jdbcUrl = DefaultConnectionConfig.h2JdbcUrl
            )
        )

    fun onConnectionTypeChanged(type: ConnectionType) {
        uiState.value = when (type) {
            ConnectionType.LOCAL -> uiState.value.copy(
                connectionType = type,
                driver = DatabaseDriver.H2.driverClassName,
                jdbcUrl = DefaultConnectionConfig.h2JdbcUrl
            )

            ConnectionType.REMOTE -> {
                val ip = uiState.value.ip.ifBlank { "localhost" }
                uiState.value.copy(
                    connectionType = type,
                    ip = ip,
                    driver = DatabaseDriver.POSTGRESQL.driverClassName,
                    jdbcUrl = formatPostgresJdbcUrl(ip)
                )
            }

            ConnectionType.OTHER -> {
                val driver = uiState.value.driver.ifBlank { DatabaseDriver.POSTGRESQL.driverClassName }
                val jdbcUrl = uiState.value.jdbcUrl.ifBlank {
                    if (driver == DatabaseDriver.H2.driverClassName) {
                        DefaultConnectionConfig.h2JdbcUrl
                    } else {
                        DefaultConnectionConfig.postgresJdbcUrl
                    }
                }
                uiState.value.copy(
                    connectionType = type,
                    driver = driver,
                    jdbcUrl = jdbcUrl
                )
            }
        }
    }

    fun onIpChanged(newValue: String) {
        uiState.value = uiState.value.copy(
            ip = newValue,
            jdbcUrl = if (uiState.value.connectionType == ConnectionType.REMOTE) {
                formatPostgresJdbcUrl(newValue)
            } else {
                uiState.value.jdbcUrl
            }
        )
    }

    fun onJdbcUrlChanged(newValue: String) {
        uiState.value = uiState.value.copy(jdbcUrl = newValue)
    }

    fun onDriverChanged(newValue: String) {
        val updatedUrl = if (uiState.value.connectionType == ConnectionType.OTHER) {
            if (newValue == DatabaseDriver.H2.driverClassName && uiState.value.jdbcUrl.startsWith("jdbc:postgresql:")) {
                DefaultConnectionConfig.h2JdbcUrl
            } else if (newValue == DatabaseDriver.POSTGRESQL.driverClassName && uiState.value.jdbcUrl.startsWith("jdbc:h2:")) {
                DefaultConnectionConfig.postgresJdbcUrl
            } else {
                uiState.value.jdbcUrl
            }
        } else {
            uiState.value.jdbcUrl
        }
        uiState.value = uiState.value.copy(
            driver = newValue,
            jdbcUrl = updatedUrl
        )
    }

    fun onUsernameChanged(newValue: String) {
        uiState.value = uiState.value.copy(username = newValue)
    }

    fun onPasswordChanged(newValue: String) {
        uiState.value = uiState.value.copy(password = newValue)
    }

    fun connect(
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            runCatching {
                val state = uiState.value
                val effectiveJdbcUrl = when (state.connectionType) {
                    ConnectionType.LOCAL -> DefaultConnectionConfig.h2JdbcUrl
                    ConnectionType.REMOTE -> formatPostgresJdbcUrl(state.ip)
                    ConnectionType.OTHER -> state.jdbcUrl
                }
                val effectiveDriver = when (state.connectionType) {
                    ConnectionType.LOCAL -> DatabaseDriver.H2.driverClassName
                    ConnectionType.REMOTE -> DatabaseDriver.POSTGRESQL.driverClassName
                    ConnectionType.OTHER -> state.driver
                }

                if (effectiveJdbcUrl.isBlank() || effectiveDriver.isBlank()) {
                    throw IllegalArgumentException("URL and Driver cannot be empty.")
                }

                uiState.value = uiState.value.copy(isLoading = true)

                withContext(Dispatchers.IO) {
                    databaseManager.init(
                        jdbcUrl = effectiveJdbcUrl,
                        driver = effectiveDriver,
                        user = if (state.connectionType == ConnectionType.LOCAL) "" else state.username,
                        password = if (state.connectionType == ConnectionType.LOCAL) "" else state.password,
                    )
                }
            }.onSuccess {
                uiState.value = uiState.value.copy(isLoading = false, isConnected = true)
                onSuccess()
            }.onFailure {
                onError(it.localizedMessage ?: "Failed to connect")
            }
        }
    }
}