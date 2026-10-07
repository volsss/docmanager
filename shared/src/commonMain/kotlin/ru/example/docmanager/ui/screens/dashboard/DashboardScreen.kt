package ru.example.docmanager.ui.screens.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.koin.compose.koinInject
import ru.example.docmanager.ui.screens.DocumentScreen
import ru.example.docmanager.ui.screens.DocumentsScreen
import ru.example.docmanager.ui.screens.SettingsScreen
import ru.example.docmanager.ui.screens.references.ReferenceScreen
import ru.example.docmanager.ui.screens.references.ReferencesScreen
import ru.example.docmanager.viewmodel.DashboardDestination
import ru.example.docmanager.viewmodel.DashboardViewModel
import ru.example.docmanager.viewmodel.SettingsViewModel

@Composable
fun DashboardScreen(
    dashboardViewModel: DashboardViewModel = koinInject(),
    settingsViewModel: SettingsViewModel = koinInject(),
    onDisconnect: () -> Unit
) {
    val destination by dashboardViewModel.destination.collectAsState()
    val projectMetadata by settingsViewModel.metadata.collectAsState()

    Scaffold(
        topBar = {
            DashboardTopBar(
                title = projectMetadata?.name ?: "Название проекта",
                onSettings = {
                    dashboardViewModel.navigateTo (
                        DashboardDestination.Settings
                    )
                },
                onDisconnect = onDisconnect,
            )
        },
        containerColor = MaterialTheme.colorScheme.primaryContainer
    ) { paddingValues ->
        DashboardContent(
            modifier = Modifier.padding(paddingValues),
            destination = destination,
            onTabSelected = dashboardViewModel::navigateTo,
            dashboardViewModel = dashboardViewModel
        )
    }
}

@Composable
fun DashboardContent (
    destination: DashboardDestination,
    onTabSelected: (DashboardDestination) -> Unit,
    dashboardViewModel: DashboardViewModel,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        DashboardTabs (
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
                Modifier.fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                when (destination) {
                    DashboardDestination.Settings -> {
                        SettingsScreen()
                    }
                    DashboardDestination.Documents -> {
                        DocumentsScreen(dashboardViewModel)
                    }
                    DashboardDestination.References -> {
                        ReferencesScreen(dashboardViewModel)
                    }

                    is DashboardDestination.Document -> {
                        DocumentScreen(destination)
                    }
                    is DashboardDestination.Reference -> {
                        ReferenceScreen(destination)
                    }
                }
            }
        }
    }
}