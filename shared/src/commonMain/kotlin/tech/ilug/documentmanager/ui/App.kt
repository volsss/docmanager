package tech.ilug.documentmanager.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ContainedLoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.compose.koinInject
import tech.ilug.documentmanager.database.DatabaseFactory
import tech.ilug.documentmanager.ui.screens.ConnectionScreen
import tech.ilug.documentmanager.ui.screens.DashboardScreen
import tech.ilug.documentmanager.viewmodel.ConnectionViewModel
import tech.ilug.documentmanager.viewmodel.DashboardViewModel
import tech.ilug.documentmanager.viewmodel.DocumentViewModel
import tech.ilug.documentmanager.viewmodel.ReferenceViewModel
import tech.ilug.documentmanager.viewmodel.SettingsViewModel

enum class ConnectionState {
    NONE, LOADING, CONNECTED
}

@Composable
fun App() {
    var state by remember { mutableStateOf(ConnectionState.NONE) }
    val connectionViewModel = koinInject<ConnectionViewModel>()
    val dashboardViewModel = koinInject<DashboardViewModel>()
    val settingsViewModel = koinInject<SettingsViewModel>()
    val documentViewModel = koinInject<DocumentViewModel>()
    val referenceViewModel = koinInject<ReferenceViewModel>()
    val scope = rememberCoroutineScope()

    when (state) {
        ConnectionState.NONE -> ConnectionScreen(
            viewModel = connectionViewModel,
            onConnect = { url, driver, user, password ->
                scope.launch {
                    withContext(Dispatchers.IO) {
                        try {
                            state = ConnectionState.LOADING
                            DatabaseFactory.init(url, driver, user, password)
                            settingsViewModel.loadMetadata()
                            referenceViewModel.loadReferenceItems()
                            state = ConnectionState.CONNECTED
                            connectionViewModel.setError(null)
                        } catch (e: Exception) {
                            state = ConnectionState.NONE
                            connectionViewModel.setError(e.message)
                            e.printStackTrace()
                        }
                    }
                }
            }
        )
        ConnectionState.CONNECTED -> DashboardScreen(
            dashboardViewModel = dashboardViewModel,
            settingsViewModel = settingsViewModel,
            documentViewModel = documentViewModel,
            referenceViewModel = referenceViewModel,
            onDisconnect = {
                DatabaseFactory.disconnect()
                state = ConnectionState.NONE
            }
        )
        ConnectionState.LOADING -> Box(
            modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center
        ) {
            ContainedLoadingIndicator(modifier = Modifier.size(128.dp))
        }
    }
}