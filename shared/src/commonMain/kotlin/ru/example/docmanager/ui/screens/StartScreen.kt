/*
 * Copyright (c) 2026 Andrew Z.
 * docmanager — a scalable document management system.
 */

package ru.example.docmanager.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import docmanager.shared.generated.resources.*
import org.jetbrains.compose.resources.stringResource
import ru.example.docmanager.di.Platform
import ru.example.docmanager.di.getPlatform
import ru.example.docmanager.ui.Utils
import ru.example.docmanager.ui.components.Dropdown
import ru.example.docmanager.ui.components.DropdownMode
import ru.example.docmanager.viewmodel.ConnectionState

@Composable
fun StartScreen (
    state: ConnectionState,
    onConnect: (String, String, String, String) -> Unit,
    onError: (String) -> Unit
) {
    val platform = remember { getPlatform() }
    var jdbcUrl by remember { mutableStateOf("") }
    var driver by remember { mutableStateOf("") }
    var user by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row (
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth().height(256.dp)
            ) {
                Text (
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
                OutlinedTextField(
                    value = jdbcUrl,
                    enabled = driver != Utils.H2_DRIVER,
                    onValueChange = { jdbcUrl = it },
                    label = { Text(stringResource(Res.string.input_jdbc_url)) },
                    supportingText = { Text(stringResource(Res.string.input_supporting_required)) },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Dropdown (
                    value = driver,
                    items = listOf(Utils.H2_DRIVER, Utils.POSTGRES_DRIVER),
                    label = stringResource(Res.string.input_driver),
                    supportingText = stringResource(Res.string.input_supporting_required),
                    onValueChange = {
                        driver = it
                        if (driver == Utils.POSTGRES_DRIVER)
                            jdbcUrl = Utils.DEFAULT_POSTGRES_JDBC
                        if (driver == Utils.H2_DRIVER)
                            jdbcUrl = if (platform == Platform.JVM) Utils.JVM_H2_JDBC
                            else Utils.ANDROID_H2_JDBC
                    },
                    mode = DropdownMode.VALUES_ONLY
                )

                AnimatedVisibility(driver == Utils.POSTGRES_DRIVER) {
                    Column (
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = user,
                            onValueChange = { user = it },
                            label = { Text(stringResource(Res.string.input_user)) },
                            singleLine = true,
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = password,
                            onValueChange = { password = it },
                            label = { Text(stringResource(Res.string.input_password)) },
                            singleLine = true,
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.fillMaxWidth(),
                            visualTransformation = PasswordVisualTransformation()
                        )
                    }
                }

                AnimatedVisibility(state is ConnectionState.Error) {
                    Spacer(Modifier.height(8.dp))
                    Text(
                        (state as ConnectionState.Error).message,
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
                        if (driver == Utils.POSTGRES_DRIVER && (user.isBlank() || password.isBlank())) {
                            onError(postgresUserAndPasswordEmpty)
                            return@Button
                        }

                        onConnect(jdbcUrl, driver, user, password)
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