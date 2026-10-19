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

package ru.example.docmanager.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import ru.example.docmanager.ui.router.Router
import ru.example.docmanager.ui.screens.ConnectionScreen

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