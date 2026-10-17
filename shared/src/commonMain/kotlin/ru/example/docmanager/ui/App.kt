/*
 * Copyright (c) 2026 Andrew Z.
 * docmanager — a scalable document management system.
 */

package ru.example.docmanager.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.*
import ru.example.docmanager.ui.router.Router
import ru.example.docmanager.ui.screens.*

@Composable
fun App(
    darkTheme: Boolean = isSystemInDarkTheme()
) {
    val isConnected = remember { mutableStateOf(false) }

    AppTheme(
        darkTheme = darkTheme
    ) {
        if (isConnected.value) {
            Router()
        } else {
            ConnectionScreen(
                onConnect = { isConnected.value = true }
            )
        }
    }
}