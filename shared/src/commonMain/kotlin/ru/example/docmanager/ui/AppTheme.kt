/*
 * Copyright (c) 2026 Andrew Z.
 * docmanager — a scalable document management system.
 */

package ru.example.docmanager.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import ru.example.docmanager.di.getColorScheme

@Composable
fun AppTheme(
    darkTheme: Boolean,
    content: @Composable () -> Unit
) {
    val colorScheme = getColorScheme(darkTheme)

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}