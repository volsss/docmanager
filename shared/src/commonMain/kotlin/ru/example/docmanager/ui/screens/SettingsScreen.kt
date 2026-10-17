/*
 * Copyright (c) 2026 Andrew Z.
 * docmanager — a scalable document management system.
 */

package ru.example.docmanager.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import docmanager.shared.generated.resources.*
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import ru.example.docmanager.ui.components.TextInput
import ru.example.docmanager.ui.viewmodel.settings.SettingsUiState
import ru.example.docmanager.ui.viewmodel.settings.SettingsViewModel

@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = koinInject()
) {
    val state = viewModel.uiState.collectAsStateWithLifecycle()

    SettingsContent (
        state = state.value,
        onProjectNameChanged = viewModel::onProjectNameChanged,
        onVersionChanged = viewModel::onVersionChanged,
        onAuthorChanged = viewModel::onAuthorChanged,
        onSave = viewModel::save,
        modifier = modifier
    )
}

@Composable
fun SettingsContent (
    state: SettingsUiState,
    onProjectNameChanged: (String) -> Unit,
    onVersionChanged: (String) -> Unit,
    onAuthorChanged: (String) -> Unit,
    onSave: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxSize().padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        TextInput(
            value = state.projectName,
            onValueChange = onProjectNameChanged,
            label = stringResource(Res.string.settings_field_project_name),
            modifier = Modifier.fillMaxWidth()
        )
        TextInput(
            value = state.version,
            onValueChange = onVersionChanged,
            label = stringResource(Res.string.settings_field_version),
            modifier = Modifier.fillMaxWidth()
        )
        TextInput(
            value = state.author,
            onValueChange = onAuthorChanged,
            label = stringResource(Res.string.settings_field_author),
            modifier = Modifier.fillMaxWidth()
        )
        TextInput(
            value = state.creationDate,
            onValueChange = { },
            label = stringResource(Res.string.settings_field_creation_date),
            enabled = false,
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = onSave
        ) {
            Text(stringResource(Res.string.settings_save_button))
        }
    }
}

@Preview
@Composable
fun SettingsScreenPreview() {
    SettingsContent(
        state = SettingsUiState(),
        onProjectNameChanged = {},
        onVersionChanged = {},
        onAuthorChanged = {},
        onSave = {}
    )
}
