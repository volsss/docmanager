/*
 * Copyright (c) 2026 Andrew Z.
 * docmanager — a scalable document management system.
 */

package ru.example.docmanager.ui.screens.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import docmanager.shared.generated.resources.Res
import docmanager.shared.generated.resources.dashboard_project_name_fallback
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import ru.example.docmanager.ui.screens.dashboard.tab.*
import ru.example.docmanager.viewmodel.DashboardDestination
import ru.example.docmanager.viewmodel.DashboardViewModel
import ru.example.docmanager.viewmodel.SettingsViewModel

@Composable
fun DashboardScreen(
    onDisconnect: () -> Unit
) {
    val dashboardViewModel = koinInject<DashboardViewModel>()
    val settingsViewModel = koinInject<SettingsViewModel>()
    val snackbarHostState = remember { SnackbarHostState() }
    val destination by dashboardViewModel.destination.collectAsStateWithLifecycle()
    val projectMetadata by settingsViewModel.metadata.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            DashboardTopBar(
                title = projectMetadata?.name ?: stringResource(Res.string.dashboard_project_name_fallback),
                onSettings = {
                    dashboardViewModel.navigateTo(
                        DashboardDestination.Settings
                    )
                },
                onDisconnect = onDisconnect,
            )
        },
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState,
                snackbar = {
                    Snackbar(
                        snackbarData = it,
                        shape = MaterialTheme.shapes.large,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                }
            )
        },
        containerColor = MaterialTheme.colorScheme.primaryContainer
    ) { paddingValues ->
        DashboardContent(
            modifier = Modifier.padding(paddingValues),
            destination = destination,
            navigateTo = dashboardViewModel::navigateTo,
            snackbarHostState = snackbarHostState
        )
    }
}

@Composable
fun DashboardContent (
    destination: DashboardDestination,
    navigateTo: (DashboardDestination) -> Unit,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        DashboardTabs(
            destination = destination,
            onTabSelected = navigateTo
        )

        Card(
            modifier = Modifier.fillMaxSize(),
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp
            ),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.background
            )
        ) {
            Column(
                Modifier.fillMaxSize().padding(16.dp)
            ) {
                when (destination) {
                    DashboardDestination.Settings -> {
                        SettingsTab()
                    }
                    DashboardDestination.Documents -> {
                        DocumentsTab(navigateTo)
                    }
                    DashboardDestination.References -> {
                        ReferencesTab(navigateTo)
                    }

                    is DashboardDestination.Document -> {
                        DocumentTab(destination)
                    }
                    is DashboardDestination.Reference -> {
                        ReferenceTab(destination, snackbarHostState)
                    }
                }
            }
        }
    }
}