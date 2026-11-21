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

package ru.example.docmanager.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

fun defaultColorScheme(isDark: Boolean): ColorScheme {
    if (isDark) {
        return darkColorScheme(
            primary = Color(0xFFB8C4FF),
            onPrimary = Color(0xFF1F2C61),
            primaryContainer = Color(0xFF374379),
            onPrimaryContainer = Color(0xFFDDE1FF),
            secondary = Color(0xFFC2C5DD),
            onSecondary = Color(0xFF2C2F42),
            secondaryContainer = Color(0xFF424659),
            onSecondaryContainer = Color(0xFFDEE1F9),
            tertiary = Color(0xFFE4BAD9),
            onTertiary = Color(0xFF43273F),
            tertiaryContainer = Color(0xFF5C3D56),
            onTertiaryContainer = Color(0xFFFFD7F4),
            error = Color(0xFFFFB4AB),
            onError = Color(0xFF690005),
            errorContainer = Color(0xFF93000A),
            onErrorContainer = Color(0xFFFFDAD6),
            background = Color(0xFF121318),
            onBackground = Color(0xFFE3E1E9),
            surface = Color(0xFF121318),
            onSurface = Color(0xFFE3E1E9),
            surfaceVariant = Color(0xFF45464F),
            onSurfaceVariant = Color(0xFFC6C5D0),
            outline = Color(0xFF90909A),
            outlineVariant = Color(0xFF45464F),
            scrim = Color(0xFF000000),
            inverseSurface = Color(0xFFE3E1E9),
            inverseOnSurface = Color(0xFF2F3036),
            inversePrimary = Color(0xFF4F5B92),
            surfaceDim = Color(0xFF121318),
            surfaceBright = Color(0xFF38393F),
            surfaceContainerLowest = Color(0xFF0D0E13),
            surfaceContainerLow = Color(0xFF1B1B21),
            surfaceContainer = Color(0xFF1F1F25),
            surfaceContainerHigh = Color(0xFF292A2F),
            surfaceContainerHighest = Color(0xFF34343A),
        )
    }
    return lightColorScheme(
        primary = Color(0xFF4F5B92),
        onPrimary = Color(0xFFFFFFFF),
        primaryContainer = Color(0xFFDDE1FF),
        onPrimaryContainer = Color(0xFF374379),
        secondary = Color(0xFF5A5D72),
        onSecondary = Color(0xFFFFFFFF),
        secondaryContainer = Color(0xFFDEE1F9),
        onSecondaryContainer = Color(0xFF424659),
        tertiary = Color(0xFF75546E),
        onTertiary = Color(0xFFFFFFFF),
        tertiaryContainer = Color(0xFFFFD7F4),
        onTertiaryContainer = Color(0xFF5C3D56),
        error = Color(0xFFBA1A1A),
        onError = Color(0xFFFFFFFF),
        errorContainer = Color(0xFFFFDAD6),
        onErrorContainer = Color(0xFF93000A),
        background = Color(0xFFFBF8FF),
        onBackground = Color(0xFF1B1B21),
        surface = Color(0xFFFBF8FF),
        onSurface = Color(0xFF1B1B21),
        surfaceVariant = Color(0xFFE2E1EC),
        onSurfaceVariant = Color(0xFF45464F),
        outline = Color(0xFF767680),
        outlineVariant = Color(0xFFC6C5D0),
        scrim = Color(0xFF000000),
        inverseSurface = Color(0xFF2F3036),
        inverseOnSurface = Color(0xFFF2F0F7),
        inversePrimary = Color(0xFFB8C4FF),
        surfaceDim = Color(0xFFDBD9E0),
        surfaceBright = Color(0xFFFBF8FF),
        surfaceContainerLowest = Color(0xFFFFFFFF),
        surfaceContainerLow = Color(0xFFF5F2FA),
        surfaceContainer = Color(0xFFEFEDF4),
        surfaceContainerHigh = Color(0xFFE9E7EF),
        surfaceContainerHighest = Color(0xFFE3E1E9),
    )
}