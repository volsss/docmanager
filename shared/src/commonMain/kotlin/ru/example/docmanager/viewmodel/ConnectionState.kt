package ru.example.docmanager.viewmodel

sealed interface ConnectionState {

    data object Disconnected : ConnectionState

    data object Connecting : ConnectionState

    data object Connected : ConnectionState

    data class Error(
        val message: String
    ) : ConnectionState
}