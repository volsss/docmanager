/*
 * Copyright (c) 2026 Andrew Z.
 * docmanager — a scalable document management system.
 */

package ru.example.docmanager.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import ru.example.docmanager.ui.DATE_FORMAT
import ru.example.docmanager.ui.router.Route
import ru.example.docmanager.ui.viewmodel.documents.DocumentsViewModel

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun DocumentsScreen(
    destination: Route.Documents,
    navigateTo: (Route) -> Unit,
    modifier: Modifier = Modifier,
    onFabActionTriggered: ((() -> Unit) -> Unit),
    viewModel: DocumentsViewModel = koinInject()
) {
    val documents = viewModel.documents.collectAsState()

    LaunchedEffect(destination) {
        viewModel.loadDocuments(destination.type)
    }

    LaunchedEffect(Unit) {
        onFabActionTriggered {
            navigateTo(
                Route.Document(destination.type)
            )
        }
    }

    Column(
        modifier = modifier.fillMaxSize().padding(horizontal = 16.dp)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(ListItemDefaults.SegmentedGap)
        ) {
            itemsIndexed(
                items = documents.value,
                key = { index, document -> "$index-${document.header.id}" }
            ) { index, document ->
                SegmentedListItem(
                    shapes = ListItemDefaults.segmentedShapes(
                        index = index,
                        count = documents.value.size
                    ),
                    colors = ListItemDefaults.segmentedColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainer
                    ),
                    onClick = {
                        navigateTo(
                            Route.Document(document.type)
                        )
                    },
                    leadingContent = {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(MaterialShapes.Slanted.toShape())
                                .background(MaterialTheme.colorScheme.secondary),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = document.header.number.toString().take(3),
                                color = MaterialTheme.colorScheme.onSecondary
                            )
                        }
                    },
                    supportingContent = {
                        Text(
                            text = "от ${
                                DATE_FORMAT.format(document.header.dischargeDate)
                            }"
                        )
                    }
                ) {
                    Text(
                        text = "${
                            stringResource(destination.type.stringResource)
                        } № ${document.header.number}"
                    )
                }
            }
        }
    }
}