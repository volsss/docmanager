/*
 * Copyright (c) 2026 Andrew Z.
 * docmanager — a scalable document management system.
 */

package ru.example.docmanager.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.example.docmanager.database.DatabaseFactory

class AppViewModel (
    private val settingsViewModel: SettingsViewModel,
    private val referenceViewModel: ReferenceViewModel,
): ViewModel() {
    val state: StateFlow<ConnectionState>
        field = MutableStateFlow<ConnectionState>(ConnectionState.Disconnected)

    fun error(message: String?) {
        state.value = ConnectionState.Error(
            message ?: "Неизвестная ошибка"
        )
    }

    fun connect(
        url: String,
        driver: String,
        user: String,
        password: String,
    ) {
        viewModelScope.launch {
            state.value = ConnectionState.Connecting

            runCatching {
                withContext(Dispatchers.IO) {
                    DatabaseFactory.init(
                        jdbcUrl = url,
                        driver = driver,
                        user = user,
                        password = password,
                    )
                }

                settingsViewModel.loadMetadata()
                referenceViewModel.loadReferenceItems()
            }.onSuccess {
                state.value = ConnectionState.Connected
            }.onFailure { error ->
                DatabaseFactory.disconnect()
                state.value = ConnectionState.Error(
                    error.message ?: "Connection failed"
                )
            }
        }
    }

    fun disconnect() {
        DatabaseFactory.disconnect()
        state.value = ConnectionState.Disconnected
    }
}