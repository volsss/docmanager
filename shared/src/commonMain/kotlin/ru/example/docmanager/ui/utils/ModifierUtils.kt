/*
 * Copyright (c) 2026 Andrew Z.
 * docmanager — a scalable document management system.
 */

package ru.example.docmanager.ui.utils

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfoV2
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.dp

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
    Column (
        modifier = Modifier.fillMaxSize().background(Color.Red),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column (
            modifier = Modifier
                .fillMaxHeight()
                .applyAdaptive(
                    whenWide = Modifier
                        .widthIn(445.dp)
                        .background(Color.Blue),
                    whenNotWide = Modifier
                        .fillMaxWidth()
                        .background(Color.Cyan)
                )
        ) { }
    }
}