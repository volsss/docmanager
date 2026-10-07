/*
 * Copyright (c) 2026 Andrew Z.
 * docmanager — a scalable document management system.
 */

package ru.example.docmanager.ui.screens.dashboard

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.ButtonGroup
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import docmanager.shared.generated.resources.Res
import docmanager.shared.generated.resources.dashboard_tabs_more
import org.jetbrains.compose.resources.stringResource
import ru.example.docmanager.viewmodel.DashboardDestination

@Composable
fun DashboardTabs(
    destination: DashboardDestination,
    onTabSelected: (DashboardDestination) -> Unit,
    modifier: Modifier = Modifier,
) {
    val documentsLabel = stringResource(DashboardDestination.Documents.stringResource)
    val referencesLabel = stringResource(DashboardDestination.References.stringResource)

    ButtonGroup(
        overflowIndicator = { menuState ->
            IconButton(
                onClick = {
                    if (menuState.isShowing) menuState.dismiss()
                    else menuState.show()
                }
            ) {
                Icon(
                    Icons.Filled.MoreVert,
                    stringResource(Res.string.dashboard_tabs_more)
                )
            }
        },
        expandedRatio = 0.1f,
        modifier = modifier
            .padding(horizontal = 16.dp)
            .widthIn(max = 700.dp),
    ) {
        toggleableItem(
            checked = destination == DashboardDestination.Documents || destination is DashboardDestination.Document,
            onCheckedChange = { onTabSelected(DashboardDestination.Documents) },
            label = documentsLabel,
            weight = 0.5f,
            icon = {
                Icon(
                    DashboardDestination.Documents.icon,
                    stringResource(DashboardDestination.Documents.stringResource)
                )
            }
        )
        toggleableItem(
            checked = destination == DashboardDestination.References || destination is DashboardDestination.Reference,
            onCheckedChange = { onTabSelected(DashboardDestination.References) },
            label = referencesLabel,
            weight = 0.5f,
            icon = {
                Icon(
                    DashboardDestination.References.icon,
                    stringResource(DashboardDestination.References.stringResource)
                )
            }
        )
    }
}