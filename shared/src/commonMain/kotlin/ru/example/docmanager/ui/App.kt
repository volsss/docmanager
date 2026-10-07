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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.koin.compose.koinInject
import ru.example.docmanager.ui.screens.dashboard.DashboardScreen
import ru.example.docmanager.ui.screens.start.StartScreen
import ru.example.docmanager.viewmodel.AppViewModel
import ru.example.docmanager.viewmodel.ConnectionState

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun App(
    viewModel: AppViewModel = koinInject()
) {
    val state by viewModel.state.collectAsState()

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