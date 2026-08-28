package tech.ilug.documentmanager.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import tech.ilug.documentmanager.viewmodel.DashboardViewModel
import tech.ilug.documentmanager.viewmodel.DocumentViewModel
import tech.ilug.documentmanager.viewmodel.ReferenceViewModel
import tech.ilug.documentmanager.viewmodel.SettingsViewModel
import tech.ilug.documentmanager.viewmodel.Tab

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun DashboardScreen (
    dashboardViewModel: DashboardViewModel,
    settingsViewModel: SettingsViewModel,
    documentViewModel: DocumentViewModel,
    referenceViewModel: ReferenceViewModel,
    onDisconnect: () -> Unit
) {
    val selectedTab by dashboardViewModel.selectedTab.collectAsState()
    val projectMetadata by settingsViewModel.metadata.collectAsState()

    Scaffold (
        topBar = {
            TopAppBar(
                title = { Text(projectMetadata?.projectName ?: "Название проекта") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                actions = {
                    IconButton (
                        onClick = { dashboardViewModel.selectTab(Tab.SETTINGS) }
                    ) {
                        Icon (
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Настройки"
                        )
                    }
                    IconButton (
                        onClick = onDisconnect
                    ) {
                        Icon (
                            imageVector = Icons.AutoMirrored.Filled.Logout,
                            contentDescription = "Отключиться"
                        )
                    }
                }
            )
        },
        containerColor = MaterialTheme.colorScheme.primaryContainer
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ButtonGroup (
                overflowIndicator = { menuState ->
                    IconButton(
                        onClick = {
                            if (menuState.isShowing) menuState.dismiss() else menuState.show()
                        }
                    ) {
                        Icon(Icons.Filled.MoreVert, "Ещё")
                    }
                },
                expandedRatio = 0.1f,
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .widthIn(max = 700.dp),
            ) {
                listOf(Tab.DOCUMENTS, Tab.REFERENCES).forEachIndexed { _, tab ->
                    toggleableItem (
                        checked = selectedTab == tab,
                        onCheckedChange = { dashboardViewModel.selectTab(tab) },
                        label = tab.text,
                        weight = 0.5f,
                        icon = {
                            Icon(tab.icon, tab.text)
                        }
                    )
                }
            }
            Spacer(Modifier.height(8.dp))

            Card (
                modifier = Modifier.fillMaxSize(),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 8.dp
                ),
                shape = RoundedCornerShape(16.dp, 16.dp, 0.dp, 0.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    contentColor = MaterialTheme.colorScheme.onBackground
                )
            ) {
                Column (
                    Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    when (selectedTab) {
                        Tab.SETTINGS -> SettingsScreen(settingsViewModel)
                        Tab.DOCUMENTS -> DocumentsScreen(
                            documentViewModel, dashboardViewModel
                        )
                        Tab.DOCUMENT -> DocumentScreen(
                            documentViewModel,
                            referenceViewModel
                        )
                        Tab.REFERENCES -> ReferencesScreen(
                            referenceViewModel, dashboardViewModel
                        )
                        Tab.REFERENCE -> ReferenceScreen(referenceViewModel)
                    }
                }
            }
        }
    }
}