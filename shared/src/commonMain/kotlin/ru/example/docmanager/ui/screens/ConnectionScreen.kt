/*
 * Copyright (c) 2026 Andrew Z.
 * docmanager — a scalable document management system.
 */

package ru.example.docmanager.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import docmanager.shared.generated.resources.*
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import ru.example.docmanager.di.Platform
import ru.example.docmanager.di.getPlatform
import ru.example.docmanager.ui.Utils
import ru.example.docmanager.ui.components.Dropdown
import ru.example.docmanager.ui.components.DropdownMode
import ru.example.docmanager.ui.components.TextInput
import ru.example.docmanager.ui.viewmodel.connection.ConnectionViewModel

@Composable
fun ConnectionScreen(
    onConnect: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ConnectionViewModel = koinInject()
) {
    val state = viewModel.uiState.collectAsStateWithLifecycle()

    if (state.value.isLoading) {
        LoadingContent()
        return
    }

    Scaffold { paddingValues ->
        ConnectionContent(
            modifier = modifier.padding(paddingValues),
            onJdbcUrlChanged = viewModel::onJdbcUrlChanged,
            onDriverChanged = {
                viewModel.onDriverChanged(it)
            },
            onUsernameChanged = viewModel::onUsernameChanged,
            onPasswordChanged = viewModel::onPasswordChanged,
            onConnectTriggered = {
                viewModel.connect {
                    onConnect()
                }
            },
            onError = viewModel::onError,
            jdbcUrl = state.value.jdbcUrl,
            driver = state.value.driver,
            username = state.value.username,
            password = state.value.password,
            errorMessage = state.value.errorMessage,
        )
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun LoadingContent() {
    Box(
        modifier = Modifier.fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        ContainedLoadingIndicator(modifier = Modifier.size(128.dp))
    }
}

@Composable
fun ConnectionContent(
    onJdbcUrlChanged: (String) -> Unit,
    onDriverChanged: (String) -> Unit,
    onUsernameChanged: (String) -> Unit,
    onPasswordChanged: (String) -> Unit,
    onConnectTriggered: () -> Unit,
    onError: (String) -> Unit,
    modifier: Modifier = Modifier,
    jdbcUrl: String = "",
    driver: String = "",
    username: String = "",
    password: String = "",
    errorMessage: String? = null
) {
    val platform = remember { getPlatform() }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth().height(256.dp)
        ) {
            Text(
                text = stringResource(Res.string.start_title),
                fontSize = 48.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                color = MaterialTheme.colorScheme.primary
            )
        }

        Column(
            modifier = Modifier.widthIn(max = 500.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            TextInput(
                value = jdbcUrl,
                label = stringResource(Res.string.input_jdbc_url),
                onValueChange = { onJdbcUrlChanged(it) },
                required = true,
                enabled = jdbcUrl != Utils.H2_DRIVER,
                modifier = Modifier.fillMaxWidth()
            )

            Dropdown(
                value = driver,
                items = listOf(Utils.H2_DRIVER, Utils.POSTGRES_DRIVER),
                label = stringResource(Res.string.input_driver),
                supportingText = stringResource(Res.string.input_supporting_required),
                onValueChange = {
                    onDriverChanged(it)
                    if (it == Utils.POSTGRES_DRIVER)
                        onJdbcUrlChanged(Utils.DEFAULT_POSTGRES_JDBC)
                    if (it == Utils.H2_DRIVER)
                        onJdbcUrlChanged(
                            if (platform == Platform.JVM)
                                Utils.JVM_H2_JDBC
                            else Utils.ANDROID_H2_JDBC
                        )
                },
                mode = DropdownMode.VALUES_ONLY
            )

            AnimatedVisibility(driver == Utils.POSTGRES_DRIVER) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TextInput(
                        value = username,
                        label = stringResource(Res.string.input_user),
                        onValueChange = { onUsernameChanged(it) },
                        required = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    TextInput(
                        value = password,
                        label = stringResource(Res.string.input_password),
                        onValueChange = { onPasswordChanged(it) },
                        required = true,
                        modifier = Modifier.fillMaxWidth(),
                        visualTransformation = PasswordVisualTransformation()
                    )
                }
            }

            AnimatedVisibility(errorMessage != null) {
                Spacer(Modifier.height(8.dp))
                Text(
                    errorMessage ?: stringResource(Res.string.unknown_error),
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Left
                )
            }

            Spacer(Modifier.heightIn(16.dp))
            val jdbcEmpty = stringResource(Res.string.start_jdbc_empty)
            val postgresUserAndPasswordEmpty = stringResource(Res.string.start_postgres_user_and_password_empty)
            Button(
                onClick = {
                    if (jdbcUrl.isBlank()) {
                        onError(jdbcEmpty)
                        return@Button
                    }
                    if (driver == Utils.POSTGRES_DRIVER && (username.isBlank() || password.isBlank())) {
                        onError(postgresUserAndPasswordEmpty)
                        return@Button
                    }

                    onConnectTriggered()
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
        onConnectTriggered = { },
        onError = { },
    )
}
