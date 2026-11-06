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

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import docmanager.shared.generated.resources.Res
import docmanager.shared.generated.resources.component_back_button
import docmanager.shared.generated.resources.route_document_types
import org.jetbrains.compose.resources.stringResource
import ru.example.docmanager.domain.document.DocumentType
import ru.example.docmanager.ui.router.Route
import ru.example.docmanager.ui.utils.adaptiveWidth

@Composable
fun DocumentTypesScreen(
    navigateTo: (Route) -> Unit,
    canGoBack: Boolean,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                navigationIcon = {
                    AnimatedVisibility(
                        visible = canGoBack,
                        enter = expandHorizontally(),
                        exit = shrinkHorizontally()
                    ) {
                        IconButton(onClick = onBack) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                                contentDescription = stringResource(Res.string.component_back_button),
                            )
                        }
                    }
                },
                title = {
                    Text(stringResource(Res.string.route_document_types))
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier.fillMaxSize().padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            DocumentTypesContent(
                navigateTo = navigateTo,
                modifier = modifier.adaptiveWidth()
            )
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun DocumentTypesContent(
    navigateTo: (Route) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier.fillMaxHeight().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(ListItemDefaults.SegmentedGap)
    ) {
        itemsIndexed(DocumentType.entries) { index, type ->
            SegmentedListItem(
                shapes = ListItemDefaults.segmentedShapes(
                    index = index,
                    count = DocumentType.entries.size
                ),
                colors = ListItemDefaults.segmentedColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer
                ),
                onClick = {
                    navigateTo(
                        Route.Documents(type)
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