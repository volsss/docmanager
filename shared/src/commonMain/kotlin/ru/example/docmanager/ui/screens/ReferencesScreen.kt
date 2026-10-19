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

package ru.example.docmanager.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringResource
import ru.example.docmanager.data.reference.ReferenceType
import ru.example.docmanager.ui.AppTheme
import ru.example.docmanager.ui.router.Route
import ru.example.docmanager.ui.utils.adaptiveWidth

@Composable
fun ReferencesScreen(
    navigateTo: (Route) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ReferencesContent(
            navigateTo = navigateTo,
            modifier = modifier.adaptiveWidth()
        )
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun ReferencesContent(
    navigateTo: (Route) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxHeight().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(ListItemDefaults.SegmentedGap)
    ) {
        itemsIndexed(ReferenceType.entries) { index, type ->
            SegmentedListItem(
                shapes = ListItemDefaults.segmentedShapes(
                    index = index,
                    count = ReferenceType.entries.size
                ),
                colors = ListItemDefaults.segmentedColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer
                ),
                onClick = {
                    navigateTo(
                        Route.Reference(type)
                    )
                },
                leadingContent = {
                    Icon(
                        imageVector = type.icon,
                        contentDescription = null
                    )
                }
            ) {
                Text(stringResource(type.stringResource))
            }
        }
    }
}

@Preview
@Composable
fun ReferencesScreenPreview() {
    AppTheme(darkTheme = false) {
        ReferencesScreen(
            navigateTo = { }
        )
    }
}

@Preview
@Composable
fun ReferencesScreenDarkThemePreview() {
    AppTheme(darkTheme = true) {
        ReferencesScreen(
            navigateTo = { }
        )
    }
}