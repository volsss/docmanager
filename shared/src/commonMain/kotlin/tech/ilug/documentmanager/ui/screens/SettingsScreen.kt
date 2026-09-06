package tech.ilug.documentmanager.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import tech.ilug.documentmanager.viewmodel.SettingsViewModel

@Composable
fun SettingsScreen(viewModel: SettingsViewModel) {
    val metadata by viewModel.metadata.collectAsState()
    val scope = rememberCoroutineScope()
    val current = metadata ?: return

    var projectName by remember(current) { mutableStateOf(current.projectName) }
    var version by remember(current) { mutableStateOf(current.version) }
    var author by remember(current) { mutableStateOf(current.author) }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Настройки проекта", style = MaterialTheme.typography.headlineSmall)
        OutlinedTextField(
            value = projectName,
            onValueChange = { projectName = it },
            label = { Text("Название проекта") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = version,
            onValueChange = { version = it },
            label = { Text("Версия") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = author,
            onValueChange = { author = it },
            label = { Text("Автор") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = current.creationDate,
            onValueChange = {},
            label = { Text("Дата создания") },
            enabled = false,
            modifier = Modifier.fillMaxWidth()
        )
        Button(onClick = {
            scope.launch {
                viewModel.updateMetadata(
                    current.copy(
                        projectName = projectName,
                        version = version,
                        author = author
                    )
                )
            }
        }) {
            Text("Сохранить настройки")
        }
    }
}