/*
 * Copyright (c) 2026 Andrew Z.
 * docmanager — a scalable document management system.
 */

package ru.example.docmanager.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import docmanager.shared.generated.resources.Res
import docmanager.shared.generated.resources.screen_references
import org.jetbrains.compose.resources.stringResource
import ru.example.docmanager.reference.ReferenceType
import ru.example.docmanager.ui.AppTheme
import ru.example.docmanager.ui.components.ScreenTopBar
import ru.example.docmanager.ui.navigation.Screen

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ReferencesScreen(
    navigateTo: (Screen) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxSize()
    ) {
        ScreenTopBar(
            title = stringResource(Res.string.screen_references),
            onBack = onBack
        )

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
                            Screen.Reference(type)
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
fun ReferencesScreenPreview() {
    AppTheme (darkTheme = false) {
        ReferencesScreen(
            navigateTo = { },
            onBack = { }
        )
    }
}

@Preview
@Composable
fun ReferencesScreenDarkThemePreview() {
    AppTheme (darkTheme = true) {
        ReferencesScreen(
            navigateTo = { },
            onBack = { }
        )
    }
}