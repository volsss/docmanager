/*
 * Copyright (c) 2026 Andrew Z.
 * docmanager — a scalable document management system.
 */

package ru.example.docmanager.di

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

actual fun getPlatform(): Platform = Platform.JVM

actual fun getDocumentProcessor(): DocumentProcessor = JvmDocumentProcessor()

@Composable
actual fun getColorScheme(darkTheme: Boolean): ColorScheme {
    return if (darkTheme) darkColorScheme() else lightColorScheme()
}