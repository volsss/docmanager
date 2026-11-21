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

package ru.example.docmanager

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import ru.example.docmanager.ui.theme.defaultColorScheme

@Composable
fun rememberAppColorScheme(): ColorScheme {
    val darkTheme = isSystemInDarkTheme()
    return remember(darkTheme) {
        defaultColorScheme(darkTheme)
    }
}