/*
 * Copyright (c) 2026 Andrew Z.
 * docmanager — a scalable document management system.
 */

package ru.example.docmanager.ui.screens.dashboard.tab

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringResource
import ru.example.docmanager.document.DocumentType
import ru.example.docmanager.ui.components.ClickableItem
import ru.example.docmanager.viewmodel.DashboardDestination

@Composable
fun DocumentsTab(
    navigateTo: (destination: DashboardDestination) -> Unit
) {
    LazyColumn (
        modifier = Modifier.fillMaxWidth()
            .clip(RoundedCornerShape(16.dp)),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        items(DocumentType.entries) { type ->
            ClickableItem(
                title = stringResource(type.stringResource),
                onClick = {
                    navigateTo(
                        DashboardDestination.Document(type)
                    )
                }
            )
        }
    }
}