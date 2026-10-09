/*
 * Copyright (c) 2026 Andrew Z.
 * docmanager — a scalable document management system.
 */

package ru.example.docmanager.ui.screens.dashboard.tab

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringResource
import ru.example.docmanager.reference.ReferenceType
import ru.example.docmanager.ui.AppTheme
import ru.example.docmanager.viewmodel.DashboardDestination

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ReferencesTab(
    navigateTo: (DashboardDestination) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxSize(),
        shape = RoundedCornerShape(16.dp, 16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.background
        )
    ) {
        LazyColumn (
            modifier = Modifier.fillMaxWidth().padding(16.dp),
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
                            DashboardDestination.Reference(type)
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
}

@Preview
@Composable
fun ReferencesTabPreview() {
    AppTheme (darkTheme = false) {
        ReferencesTab(
            navigateTo = { }
        )
    }
}

@Preview
@Composable
fun ReferencesTabDarkThemePreview() {
    AppTheme (darkTheme = true) {
        ReferencesTab(
            navigateTo = { }
        )
    }
}