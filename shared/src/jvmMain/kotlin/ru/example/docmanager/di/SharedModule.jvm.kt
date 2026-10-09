/*
 * Copyright (c) 2026 Andrew Z.
 * docmanager — a scalable document management system.
 */

package ru.example.docmanager.di

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import com.materialkolor.dynamiccolor.ColorSpec
import com.materialkolor.hct.Hct
import com.materialkolor.ktx.from
import com.materialkolor.scheme.SchemeExpressive

actual fun getPlatform(): Platform = Platform.JVM

actual fun getDocumentProcessor(): DocumentProcessor = JvmDocumentProcessor()

@Composable
actual fun getColorScheme(darkTheme: Boolean): ColorScheme {
    val accentColor = remember { getWindowsAccentColor() }

    val scheme = remember(accentColor) {
        accentColor?.let {
            SchemeExpressive(
                sourceColorHct = Hct.from(it),
                isDark = darkTheme,
                contrastLevel = 1.0,
                specVersion = ColorSpec.SpecVersion.SPEC_2025
            )
        }
    }

    return if (scheme != null) {
        if (darkTheme)
            darkColorScheme(
                primary = Color(scheme.primary),
                onPrimary = Color(scheme.onPrimary),
                primaryContainer = Color(scheme.primaryContainer),
                onPrimaryContainer = Color(scheme.onPrimaryContainer),
                inversePrimary = Color(scheme.inversePrimary),
                secondary = Color(scheme.secondary),
                onSecondary = Color(scheme.onSecondary),
                secondaryContainer = Color(scheme.secondaryContainer),
                onSecondaryContainer = Color(scheme.onSecondaryContainer),
                tertiary = Color(scheme.tertiary),
                onTertiary = Color(scheme.onTertiary),
                tertiaryContainer = Color(scheme.tertiaryContainer),
                onTertiaryContainer = Color(scheme.onTertiaryContainer),
                background = Color(scheme.background),
                onBackground = Color(scheme.onBackground),
                surface = Color(scheme.surface),
                onSurface = Color(scheme.onSurface),
                surfaceVariant = Color(scheme.surfaceVariant),
                onSurfaceVariant = Color(scheme.onSurfaceVariant),
                surfaceTint = Color(scheme.surfaceTint),
                inverseSurface = Color(scheme.inverseSurface),
                inverseOnSurface = Color(scheme.inverseOnSurface),
                error = Color(scheme.error),
                onError = Color(scheme.onError),
                errorContainer = Color(scheme.errorContainer),
                onErrorContainer = Color(scheme.onErrorContainer),
                outline = Color(scheme.outline),
                outlineVariant = Color(scheme.outlineVariant),
                scrim = Color(scheme.scrim),
                surfaceBright = Color(scheme.surfaceBright),
                surfaceContainer = Color(scheme.surfaceContainer),
                surfaceContainerHigh = Color(scheme.surfaceContainerHigh),
                surfaceContainerHighest = Color(scheme.surfaceContainerHighest),
                surfaceContainerLow = Color(scheme.surfaceContainerLow),
                surfaceContainerLowest = Color(scheme.surfaceContainerLowest),
                surfaceDim = Color(scheme.surfaceDim),
                primaryFixed = Color(scheme.primaryFixed),
                primaryFixedDim = Color(scheme.primaryFixedDim),
                onPrimaryFixed = Color(scheme.onPrimaryFixed),
                onPrimaryFixedVariant = Color(scheme.onPrimaryFixedVariant),
                secondaryFixed = Color(scheme.secondaryFixed),
                secondaryFixedDim = Color(scheme.secondaryFixedDim),
                onSecondaryFixed = Color(scheme.onSecondaryFixed),
                onSecondaryFixedVariant = Color(scheme.onSecondaryFixedVariant),
                tertiaryFixed = Color(scheme.tertiaryFixed),
                tertiaryFixedDim = Color(scheme.tertiaryFixedDim),
                onTertiaryFixed = Color(scheme.onTertiaryFixed),
                onTertiaryFixedVariant = Color(scheme.onTertiaryFixedVariant),
            )
        else
            lightColorScheme(
                primary = Color(scheme.primary),
                onPrimary = Color(scheme.onPrimary),
                primaryContainer = Color(scheme.primaryContainer),
                onPrimaryContainer = Color(scheme.onPrimaryContainer),
                inversePrimary = Color(scheme.inversePrimary),
                secondary = Color(scheme.secondary),
                onSecondary = Color(scheme.onSecondary),
                secondaryContainer = Color(scheme.secondaryContainer),
                onSecondaryContainer = Color(scheme.onSecondaryContainer),
                tertiary = Color(scheme.tertiary),
                onTertiary = Color(scheme.onTertiary),
                tertiaryContainer = Color(scheme.tertiaryContainer),
                onTertiaryContainer = Color(scheme.onTertiaryContainer),
                background = Color(scheme.background),
                onBackground = Color(scheme.onBackground),
                surface = Color(scheme.surface),
                onSurface = Color(scheme.onSurface),
                surfaceVariant = Color(scheme.surfaceVariant),
                onSurfaceVariant = Color(scheme.onSurfaceVariant),
                surfaceTint = Color(scheme.surfaceTint),
                inverseSurface = Color(scheme.inverseSurface),
                inverseOnSurface = Color(scheme.inverseOnSurface),
                error = Color(scheme.error),
                onError = Color(scheme.onError),
                errorContainer = Color(scheme.errorContainer),
                onErrorContainer = Color(scheme.onErrorContainer),
                outline = Color(scheme.outline),
                outlineVariant = Color(scheme.outlineVariant),
                scrim = Color(scheme.scrim),
                surfaceBright = Color(scheme.surfaceBright),
                surfaceContainer = Color(scheme.surfaceContainer),
                surfaceContainerHigh = Color(scheme.surfaceContainerHigh),
                surfaceContainerHighest = Color(scheme.surfaceContainerHighest),
                surfaceContainerLow = Color(scheme.surfaceContainerLow),
                surfaceContainerLowest = Color(scheme.surfaceContainerLowest),
                surfaceDim = Color(scheme.surfaceDim),
                primaryFixed = Color(scheme.primaryFixed),
                primaryFixedDim = Color(scheme.primaryFixedDim),
                onPrimaryFixed = Color(scheme.onPrimaryFixed),
                onPrimaryFixedVariant = Color(scheme.onPrimaryFixedVariant),
                secondaryFixed = Color(scheme.secondaryFixed),
                secondaryFixedDim = Color(scheme.secondaryFixedDim),
                onSecondaryFixed = Color(scheme.onSecondaryFixed),
                onSecondaryFixedVariant = Color(scheme.onSecondaryFixedVariant),
                tertiaryFixed = Color(scheme.tertiaryFixed),
                tertiaryFixedDim = Color(scheme.tertiaryFixedDim),
                onTertiaryFixed = Color(scheme.onTertiaryFixed),
                onTertiaryFixedVariant = Color(scheme.onTertiaryFixedVariant),
            )
    } else {
        if (darkTheme) darkColorScheme()
        else lightColorScheme()
    }
}