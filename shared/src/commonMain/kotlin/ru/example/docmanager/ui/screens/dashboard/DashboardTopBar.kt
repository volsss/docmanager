/*
 * Copyright (c) 2026 Andrew Z.
 * docmanager — a scalable document management system.
 */

package ru.example.docmanager.ui.screens.dashboard

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import docmanager.shared.generated.resources.Res
import docmanager.shared.generated.resources.dashboard_action_disconnect
import docmanager.shared.generated.resources.dashboard_action_settings
import org.jetbrains.compose.resources.stringResource

@Composable
fun DashboardTopBar(
    title: String,
    onSettings: () -> Unit,
    onDisconnect: () -> Unit,
) {
    TopAppBar(
        title = {
            Text(title)
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            actionIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
        ),
        actions = {
            IconButton(onClick = onSettings) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = stringResource(Res.string.dashboard_action_settings),
                )
            }

            IconButton(onClick = onDisconnect) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Logout,
                    contentDescription = stringResource(Res.string.dashboard_action_disconnect),
                )
            }
        },
    )
}