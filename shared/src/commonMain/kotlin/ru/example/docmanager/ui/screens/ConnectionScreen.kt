/*
 * Copyright (c) 2026 Andrew Z.
 * docmanager — a scalable document management system.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License v3.0.
 *
 * Distributed WITHOUT ANY WARRANTY. See the GNU General Public License
 * for more details: <https://gnu.org>
 */

package ru.example.docmanager.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.graphics.shapes.RoundedPolygon
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import docmanager.shared.generated.resources.*
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import ru.example.docmanager.data.database.DatabaseDriver
import ru.example.docmanager.ui.components.Dropdown
import ru.example.docmanager.ui.components.DropdownMode
import ru.example.docmanager.ui.components.TextInput
import ru.example.docmanager.ui.utils.adaptiveWidth
import ru.example.docmanager.ui.viewmodel.connection.ConnectionType
import ru.example.docmanager.ui.viewmodel.connection.ConnectionViewModel

@Composable
fun ConnectionScreen(
    onConnect: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ConnectionViewModel = koinViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    if (state.isLoading) {
        LoadingContent()
        return
    }

    ConnectionContent(
        modifier = modifier,
        connectionType = state.connectionType,
        onConnectionTypeChanged = viewModel::onConnectionTypeChanged,
        ip = state.ip,
        onIpChanged = viewModel::onIpChanged,
        onJdbcUrlChanged = viewModel::onJdbcUrlChanged,
        onDriverChanged = viewModel::onDriverChanged,
        onUsernameChanged = viewModel::onUsernameChanged,
        onPasswordChanged = viewModel::onPasswordChanged,
        onConnectTriggered = {
            viewModel.connect(
                onSuccess = onConnect,
                onError = {
                    // TODO: show error message
                }
            )
        },
        jdbcUrl = state.jdbcUrl,
        driver = state.driver,
        username = state.username,
        password = state.password
    )
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun LoadingContent() {
    Box(
        modifier = Modifier.fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        ContainedLoadingIndicator(modifier = Modifier.size(128.dp))
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ConnectionContent(
    modifier: Modifier = Modifier,
    connectionType: ConnectionType = ConnectionType.LOCAL,
    onConnectionTypeChanged: (ConnectionType) -> Unit = { },
    ip: String = "",
    onIpChanged: (String) -> Unit = { },
    onJdbcUrlChanged: (String) -> Unit = { },
    onDriverChanged: (String) -> Unit = { },
    onUsernameChanged: (String) -> Unit = { },
    onPasswordChanged: (String) -> Unit = { },
    onConnectTriggered: () -> Unit = { },
    jdbcUrl: String = "",
    driver: String = "",
    username: String = "",
    password: String = ""
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val jdbcEmpty = stringResource(Res.string.start_error_jdbc_empty)
    val ipEmpty = stringResource(Res.string.start_error_ip_empty)
    val postgresUserAndPasswordEmpty = stringResource(Res.string.start_error_postgres_user_and_password_empty)

    Scaffold(
        modifier = modifier,
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Box(
            modifier = Modifier.fillMaxSize().padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier.adaptiveWidth(546.dp)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = stringResource(Res.string.start_title),
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(32.dp))

                Column(
                    verticalArrangement = Arrangement.spacedBy(ListItemDefaults.SegmentedGap)
                ) {
                    ConnectionOption(
                        index = 0,
                        count = 3,
                        selected = connectionType == ConnectionType.LOCAL,
                        onClick = { onConnectionTypeChanged(ConnectionType.LOCAL) },
                        iconPolygon = MaterialShapes.Circle,
                        supportingText = stringResource(Res.string.start_option_local_supporting),
                        text = stringResource(Res.string.start_option_local)
                    )
                    ConnectionOption(
                        index = 1,
                        count = 3,
                        selected = connectionType == ConnectionType.REMOTE,
                        onClick = { onConnectionTypeChanged(ConnectionType.REMOTE) },
                        iconPolygon = MaterialShapes.Gem,
                        iconColor = MaterialTheme.colorScheme.tertiary,
                        supportingText = stringResource(Res.string.start_option_remote_supporting),
                        text = stringResource(Res.string.start_option_remote)
                    )
                    ConnectionOption(
                        index = 2,
                        count = 3,
                        selected = connectionType == ConnectionType.OTHER,
                        onClick = { onConnectionTypeChanged(ConnectionType.OTHER) },
                        iconPolygon = MaterialShapes.Pill,
                        iconColor = MaterialTheme.colorScheme.secondary,
                        supportingText = stringResource(Res.string.start_option_other_supporting),
                        text = stringResource(Res.string.start_option_other)
                    )
                }

                Spacer(Modifier.height(8.dp))

                AnimatedVisibility(connectionType == ConnectionType.REMOTE) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        TextInput(
                            value = ip,
                            label = stringResource(Res.string.start_input_ip),
                            onValueChange = { onIpChanged(it) },
                            required = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        TextInput(
                            value = username,
                            label = stringResource(Res.string.start_input_user),
                            onValueChange = { onUsernameChanged(it) },
                            required = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        TextInput(
                            value = password,
                            label = stringResource(Res.string.start_input_password),
                            onValueChange = { onPasswordChanged(it) },
                            required = true,
                            modifier = Modifier.fillMaxWidth(),
                            visualTransformation = PasswordVisualTransformation()
                        )
                    }
                }

                AnimatedVisibility(connectionType == ConnectionType.OTHER) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        TextInput(
                            value = jdbcUrl,
                            label = stringResource(Res.string.start_input_jdbc_url),
                            onValueChange = { onJdbcUrlChanged(it) },
                            required = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Dropdown(
                            value = driver,
                            items = DatabaseDriver.entries.map { it.driverClassName },
                            label = stringResource(Res.string.start_input_driver),
                            supportingText = stringResource(Res.string.component_input_required),
                            onValueChange = { onDriverChanged(it) },
                            mode = DropdownMode.VALUES_ONLY
                        )

                        TextInput(
                            value = username,
                            label = stringResource(Res.string.start_input_user),
                            onValueChange = { onUsernameChanged(it) },
                            modifier = Modifier.fillMaxWidth()
                        )

                        TextInput(
                            value = password,
                            label = stringResource(Res.string.start_input_password),
                            onValueChange = { onPasswordChanged(it) },
                            modifier = Modifier.fillMaxWidth(),
                            visualTransformation = PasswordVisualTransformation()
                        )
                    }
                }

                Spacer(Modifier.height(32.dp))

                Button(
                    onClick = {
                        when (connectionType) {
                            ConnectionType.LOCAL -> {
                                onConnectTriggered()
                            }

                            ConnectionType.REMOTE -> {
                                if (ip.isBlank()) {
                                    scope.launch {
                                        snackbarHostState.showSnackbar(ipEmpty)
                                    }
                                    return@Button
                                }
                                if (username.isBlank() || password.isBlank()) {
                                    scope.launch {
                                        snackbarHostState.showSnackbar(postgresUserAndPasswordEmpty)
                                    }
                                    return@Button
                                }
                                onConnectTriggered()
                            }

                            ConnectionType.OTHER -> {
                                if (jdbcUrl.isBlank()) {
                                    scope.launch {
                                        snackbarHostState.showSnackbar(jdbcEmpty)
                                    }
                                    return@Button
                                }
                                if (driver == DatabaseDriver.POSTGRESQL.driverClassName && (username.isBlank() || password.isBlank())) {
                                    scope.launch {
                                        snackbarHostState.showSnackbar(postgresUserAndPasswordEmpty)
                                    }
                                    return@Button
                                }
                                onConnectTriggered()
                            }
                        }
                    },
                    contentPadding = ButtonDefaults.LargeContentPadding,
                ) {
                    Text(
                        stringResource(Res.string.start_connect_button),
                        style = ButtonDefaults.textStyleFor(ButtonDefaults.LargeContainerHeight)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ConnectionOption(
    index: Int,
    count: Int,
    selected: Boolean,
    onClick: () -> Unit,
    iconPolygon: RoundedPolygon,
    iconColor: Color = MaterialTheme.colorScheme.primary,
    supportingText: String,
    text: String
) {
    SegmentedListItem(
        selected = selected,
        onClick = onClick,
        shapes = ListItemDefaults.segmentedShapes(
            index = index,
            count = count
        ),
        colors = ListItemDefaults.segmentedColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        ),
        leadingContent = {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(iconPolygon.toShape())
                    .background(iconColor),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = text.firstOrNull()?.toString() ?: "",
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.contentColorFor(iconColor)
                )
            }
        },
        supportingContent = {
            Text(supportingText)
        }
    ) {
        Text(text)
    }
}

@Preview
@Composable
fun LoadingContentPreview() {
    LoadingContent()
}

@Preview
@Composable
fun ConnectionScreenPreview() {
    ConnectionContent(
        onJdbcUrlChanged = { },
        onDriverChanged = { },
        onUsernameChanged = { },
        onPasswordChanged = { },
        onConnectTriggered = { }
    )
}
