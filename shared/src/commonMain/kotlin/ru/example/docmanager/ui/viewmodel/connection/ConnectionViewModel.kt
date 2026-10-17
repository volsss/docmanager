/*
 * Copyright (c) 2026 Andrew Z.
 * docmanager — a scalable document management system.
 */

package ru.example.docmanager.ui.viewmodel.connection

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.example.docmanager.data.database.DatabaseManager

class ConnectionViewModel(
    private val databaseManager: DatabaseManager
) : ViewModel() {
    val uiState: StateFlow<ConnectionUiState>
        field = MutableStateFlow(ConnectionUiState())

    fun onJdbcUrlChanged(newValue: String) {
        uiState.value = uiState.value.copy(jdbcUrl = newValue)
    }

    fun onDriverChanged(newValue: String) {
        uiState.value = uiState.value.copy(driver = newValue)
    }

    fun onUsernameChanged(newValue: String) {
        uiState.value = uiState.value.copy(username = newValue)
    }

    fun onPasswordChanged(newValue: String) {
        uiState.value = uiState.value.copy(password = newValue)
    }

    fun onError(error: String) {
        uiState.value = uiState.value.copy(
            errorMessage = error,
            isLoading = false,
            isConnected = false
        )
    }

    fun connect(
        onSuccess: () -> Unit,
    ) {
        viewModelScope.launch {
            runCatching {
                if (uiState.value.jdbcUrl.isBlank() || uiState.value.driver.isBlank()) {
                    throw IllegalArgumentException("URL and Driver cannot be empty.")
                }

                uiState.value = uiState.value.copy(isLoading = true, errorMessage = null)

                withContext(Dispatchers.IO) {
                    databaseManager.init(
                        jdbcUrl = uiState.value.jdbcUrl,
                        driver = uiState.value.driver,
                        user = uiState.value.username,
                        password = uiState.value.password,
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