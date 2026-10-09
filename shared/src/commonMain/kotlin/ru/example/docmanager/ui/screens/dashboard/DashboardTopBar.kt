/*
 * Copyright (c) 2026 Andrew Z.
 * docmanager — a scalable document management system.
 */

package ru.example.docmanager.ui.screens.dashboard

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.tooling.preview.Preview
import docmanager.shared.generated.resources.Res
import docmanager.shared.generated.resources.dashboard_action_disconnect
import docmanager.shared.generated.resources.dashboard_action_settings
import docmanager.shared.generated.resources.dashboard_project_name_fallback
import org.jetbrains.compose.resources.stringResource
import ru.example.docmanager.ui.AppTheme

@Composable
fun DashboardTopBar(
    title: String,
    canNavigateBack: Boolean = false,
    onBack: () -> Unit = {},
    onSettings: () -> Unit,
    onDisconnect: () -> Unit,
) {
    TopAppBar(
        navigationIcon = {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                AnimatedVisibility(visible = canNavigateBack) {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                        )
                    }
                }
            }
        },
        title = {
            Text(title)
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            actionIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            navigationIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
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

@Preview
@Composable
fun DashboardTopBarPreview() {
    AppTheme (
        darkTheme = false
    ) {
        DashboardTopBar(
            title = stringResource(Res.string.dashboard_project_name_fallback),
            canNavigateBack = false,
            onBack = { },
            onSettings = { },
            onDisconnect = { },
        )
    }
}

@Preview
@Composable
fun DashboardTopBarWithBackPreview() {
    AppTheme (
        darkTheme = false
    ) {
        DashboardTopBar(
            title = stringResource(Res.string.dashboard_project_name_fallback),
            canNavigateBack = true,
            onBack = { },
            onSettings = { },
            onDisconnect = { },
        )
    }
}