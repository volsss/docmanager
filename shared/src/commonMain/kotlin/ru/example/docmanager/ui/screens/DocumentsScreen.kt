/*
 * Copyright (c) 2026 Andrew Z.
 * docmanager — a scalable document management system.
 */

package ru.example.docmanager.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Description
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import docmanager.shared.generated.resources.Res
import docmanager.shared.generated.resources.screen_documents
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import ru.example.docmanager.document.DocumentType
import ru.example.docmanager.ui.Utils.DATE_FORMAT
import ru.example.docmanager.ui.components.ScreenTopBar
import ru.example.docmanager.ui.navigation.Screen
import ru.example.docmanager.viewmodel.DocumentsViewModel

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun DocumentsScreen(
    destination: Screen.Documents,
    navigateTo: (Screen) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: DocumentsViewModel = koinInject()
) {
    val documents = viewModel.documents.collectAsState()

    Column(
        modifier = modifier.fillMaxSize()
    ) {
        ScreenTopBar(
            title = stringResource(Res.string.screen_documents),
            onBack = onBack
        )

        Text(text = stringResource(destination.type.stringResource))

        LazyColumn (
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(ListItemDefaults.SegmentedGap)
        ) {
            itemsIndexed(
                items = documents.value,
                key = { index, document -> "$index-${document.header.id}" }
            ) { index, document ->
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
                            Screen.Document(document.type)
                        )
                    },
                    leadingContent = {
                        Icon(
                            imageVector = Icons.Rounded.Description,
                            contentDescription = null
                        )
                    }
                ) {
                    Text(
                        text = "№ ${document.header.number}"
                    )
                    Text(
                        text = "от ${DATE_FORMAT.format(document.header.dischargeDate)}"
                    )
                }
            }

            item {
                Button(
                    onClick = {
                        navigateTo(
                            Screen.Document(destination.type)
                        )
                    }
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Add,
                        contentDescription = null
                    )
                    Text(
                        text = "Новый документ"
                    )
                }
            }
        }
    }
}