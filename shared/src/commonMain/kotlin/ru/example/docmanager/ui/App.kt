package ru.example.docmanager.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ContainedLoadingIndicator
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.compose.koinInject
import ru.example.docmanager.database.DatabaseFactory
import ru.example.docmanager.ui.screens.DashboardScreen
import ru.example.docmanager.ui.screens.start.StartScreen
import ru.example.docmanager.viewmodel.AppViewModel
import ru.example.docmanager.viewmodel.ConnectionState
import ru.example.docmanager.viewmodel.DashboardViewModel
import ru.example.docmanager.viewmodel.DocumentViewModel
import ru.example.docmanager.viewmodel.ReferenceViewModel
import ru.example.docmanager.viewmodel.SettingsViewModel

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun App(
    viewModel: AppViewModel = koinInject()
) {
    val state by viewModel.state.collectAsState()
    val dashboardViewModel = koinInject<DashboardViewModel>()
    val settingsViewModel = koinInject<SettingsViewModel>()
    val documentViewModel = koinInject<DocumentViewModel>()
    val referenceViewModel = koinInject<ReferenceViewModel>()

    when (state) {
        ConnectionState.Disconnected,
        is ConnectionState.Error -> {
            StartScreen(
                viewModel = viewModel,
                state = state,
                onConnect = viewModel::connect
            )
        }
        ConnectionState.Connected -> {
            DashboardScreen(
                dashboardViewModel = dashboardViewModel,
                settingsViewModel = settingsViewModel,
                documentViewModel = documentViewModel,
                referenceViewModel = referenceViewModel,
                onDisconnect = viewModel::disconnect
            )
        }
        ConnectionState.Connecting -> {
            Box (
                modifier = Modifier.fillMaxSize()
                    .background(MaterialTheme.colorScheme.background),
                contentAlignment = Alignment.Center
            ) {
                ContainedLoadingIndicator(modifier = Modifier.size(128.dp))
            }
        }
    }
}