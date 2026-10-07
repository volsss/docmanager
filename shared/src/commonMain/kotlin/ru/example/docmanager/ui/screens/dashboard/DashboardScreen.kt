/*
 * Copyright (c) 2026 Andrew Z.
 * docmanager — a scalable document management system.
 */

package ru.example.docmanager.ui.screens.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import docmanager.shared.generated.resources.Res
import docmanager.shared.generated.resources.dashboard_project_name_fallback
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import ru.example.docmanager.ui.screens.dashboard.tab.DocumentTab
import ru.example.docmanager.ui.screens.dashboard.tab.DocumentsTab
import ru.example.docmanager.ui.screens.dashboard.tab.SettingsTab
import ru.example.docmanager.ui.screens.dashboard.tab.ReferenceTab
import ru.example.docmanager.ui.screens.dashboard.tab.ReferencesTab
import ru.example.docmanager.viewmodel.DashboardDestination
import ru.example.docmanager.viewmodel.DashboardViewModel
import ru.example.docmanager.viewmodel.SettingsViewModel

@Composable
fun DashboardScreen(
    dashboardViewModel: DashboardViewModel = koinInject(),
    settingsViewModel: SettingsViewModel = koinInject(),
    onDisconnect: () -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val destination by dashboardViewModel.destination.collectAsState()
    val projectMetadata by settingsViewModel.metadata.collectAsState()

    Scaffold(
        topBar = {
            DashboardTopBar(
                title = projectMetadata?.name ?: stringResource(
                    Res.string.dashboard_project_name_fallback
                ),
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
            onTabSelected = dashboardViewModel::navigateTo,
            dashboardViewModel = dashboardViewModel,
            snackbarHostState = snackbarHostState
        )
    }
}

@Composable
fun DashboardContent (
    destination: DashboardDestination,
    onTabSelected: (DashboardDestination) -> Unit,
    dashboardViewModel: DashboardViewModel,
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
            onTabSelected = onTabSelected
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
                        DocumentsTab(dashboardViewModel)
                    }
                    DashboardDestination.References -> {
                        ReferencesTab(dashboardViewModel)
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