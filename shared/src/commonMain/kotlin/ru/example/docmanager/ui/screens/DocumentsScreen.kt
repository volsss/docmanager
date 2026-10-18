/*
 * Copyright (c) 2026 Andrew Z.
 * docmanager — a scalable document management system.
 */

package ru.example.docmanager.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import docmanager.shared.generated.resources.Res
import docmanager.shared.generated.resources.documents_empty_content
import docmanager.shared.generated.resources.documents_empty_new_button
import org.jetbrains.compose.resources.stringResource
import ru.example.docmanager.data.document.Document
import ru.example.docmanager.data.document.DocumentType
import ru.example.docmanager.ui.DATE_FORMAT
import ru.example.docmanager.ui.router.Route
import ru.example.docmanager.ui.viewmodel.documents.DocumentsViewModel

@Composable
fun DocumentsScreen(
    destination: Route.Documents,
    navigateTo: (Route) -> Unit,
    registerFabClickAction: ((() -> Unit)?) -> Unit,
    viewModel: DocumentsViewModel,
    modifier: Modifier = Modifier,
) {
    val documents by viewModel.documents.collectAsStateWithLifecycle()

    LaunchedEffect(destination.type) {
        viewModel.reloadDocumentList()
    }

    LaunchedEffect(registerFabClickAction, documents) {
        if (documents.isNullOrEmpty()) {
            registerFabClickAction(null)
            return@LaunchedEffect
        }
        registerFabClickAction {
            navigateTo(Route.Form(destination.type))
        }
    }

    when {
        documents == null -> {
            LoadingContent(modifier = modifier)
            return
        }

        documents!!.isEmpty() -> {
            EmptyContent(
                onNewDocumentClick = {
                    navigateTo(Route.Form(destination.type))
                },
                modifier = modifier
            )
            return
        }

        else -> {
            DocumentsContent(
                documentType = destination.type,
                documents = documents!!,
                navigateTo = navigateTo,
                modifier = modifier,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun DocumentsContent(
    documentType: DocumentType,
    documents: List<Document>,
    navigateTo: (Route) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(ListItemDefaults.SegmentedGap)
        ) {
            itemsIndexed(
                items = documents,
                key = { index, document -> "$index-${document.header.id}" }
            ) { index, document ->
                SegmentedListItem(
                    shapes = ListItemDefaults.segmentedShapes(
                        index = index,
                        count = documents.size
                    ),
                    colors = ListItemDefaults.segmentedColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainer
                    ),
                    onClick = {
                        navigateTo(
                            Route.Form(
                                type = document.type,
                                document = document
                            )
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
                            stringResource(documentType.stringResource)
                        } № ${document.header.number}"
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptyContent(
    onNewDocumentClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(48.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(32.dp)
        ) {
            Text(
                text = stringResource(Res.string.documents_empty_content),
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center
            )
            FilledTonalButton(
                onClick = onNewDocumentClick,
                modifier = Modifier
                    .height(ButtonDefaults.MediumContainerHeight),
                contentPadding = ButtonDefaults.MediumContentPadding
            ) {
                Icon(
                    imageVector = Icons.Rounded.Add,
                    contentDescription = stringResource(Res.string.documents_empty_new_button),
                    modifier = Modifier.size(ButtonDefaults.MediumIconSize)
                )
                Spacer(Modifier.width(ButtonDefaults.MediumIconSpacing))
                Text(
                    text = stringResource(Res.string.documents_empty_new_button),
                    style = ButtonDefaults.textStyleFor(
                        ButtonDefaults.MediumContainerHeight
                    ),
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun LoadingContent(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        ContainedLoadingIndicator(modifier = Modifier.size(128.dp))
    }
}

@Preview
@Composable
fun EmptyContentPreview() {
    EmptyContent(
        onNewDocumentClick = { }
    )
}
