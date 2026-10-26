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

package ru.example.docmanager.ui.utils

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfoV2
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun Modifier.adaptiveWidth(max: Dp = 891.dp): Modifier {
    return this.widthIn(max = max).fillMaxWidth()
}

@Composable
fun Modifier.applyAdaptive(
    whenWide: Modifier = Modifier,
    whenNotWide: Modifier = Modifier,
): Modifier {
    val windowSizeClass = currentWindowAdaptiveInfoV2().windowSizeClass
    val isWide = windowSizeClass.isWidthAtLeastBreakpoint(
        891
    )
    return if (isWide) {
        this.then(whenWide)
    } else {
        this.then(whenNotWide)
    }
}

@PreviewScreenSizes
@Composable
fun TestAdaptive() {
    Column(
        modifier = Modifier.fillMaxSize().background(Color.Red),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .adaptiveWidth()
                .applyAdaptive(
                    whenWide = Modifier.background(Color.Blue),
                    whenNotWide = Modifier.background(Color.Cyan)
                )
        ) { }
    }
}