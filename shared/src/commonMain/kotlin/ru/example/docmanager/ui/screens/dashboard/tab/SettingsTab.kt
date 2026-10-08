/*
 * Copyright (c) 2026 Andrew Z.
 * docmanager — a scalable document management system.
 */

package ru.example.docmanager.ui.screens.dashboard.tab

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import docmanager.shared.generated.resources.*
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import ru.example.docmanager.viewmodel.SettingsViewModel

@Composable
fun SettingsTab() {
    val viewModel = koinInject<SettingsViewModel>()
    val metadata by viewModel.metadata.collectAsState()
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        viewModel.loadMetadata()
    }

    val current = metadata ?: return

    var projectName by remember(current) { mutableStateOf(current.name) }
    var version by remember(current) { mutableStateOf(current.version) }
    var author by remember(current) { mutableStateOf(current.author) }

    Column(
        modifier = Modifier.fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = stringResource(Res.string.settings_title),
            style = MaterialTheme.typography.headlineSmall
        )
        OutlinedTextField(
            value = projectName,
            onValueChange = { projectName = it },
            label = { Text(stringResource(Res.string.settings_field_project_name)) },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = version,
            onValueChange = { version = it },
            label = { Text(stringResource(Res.string.settings_field_version)) },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = author,
            onValueChange = { author = it },
            label = { Text(stringResource(Res.string.settings_field_author)) },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = current.creationDate,
            onValueChange = {},
            label = { Text(stringResource(Res.string.settings_field_creation_date)) },
            enabled = false,
            modifier = Modifier.fillMaxWidth()
        )

        Button(onClick = {
            scope.launch {
                viewModel.updateMetadata(
                    current.copy(
                        name = projectName,
                        version = version,
                        author = author
                    )
                )
            }
        }) {
            Text(stringResource(Res.string.settings_save_button))
        }
    }
}