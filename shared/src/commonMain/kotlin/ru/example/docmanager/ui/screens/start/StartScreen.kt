package ru.example.docmanager.ui.screens.start

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
import ru.example.docmanager.viewmodel.ConnectionViewModel

@Composable
fun StartScreen(
    viewModel: ConnectionViewModel,
    onConnect: (String, String, String, String) -> Unit
) {
    val platform = remember { getPlatform() }
    var jdbcUrl by remember { mutableStateOf("") }
    var driver by remember { mutableStateOf<String?>(null) }
    var user by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val error by viewModel.error.collectAsState()

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
                    text = stringResource(Res.string.start_screen_title),
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

                DriverDropDown(
                    modifier = Modifier.fillMaxWidth(),
                    selectedItem = driver,
                    onItemSelected = {
                        driver = it
                        if (driver == Utils.POSTGRES_DRIVER)
                            jdbcUrl = Utils.DEFAULT_POSTGRES_JDBC
                        if (driver == Utils.H2_DRIVER)
                            jdbcUrl = if (platform == Platform.JVM) Utils.JVM_H2_JDBC
                            else Utils.ANDROID_H2_JDBC
                    }
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

                AnimatedVisibility(error != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        error.orEmpty(),
                        color = MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Left
                    )
                }

                Spacer(modifier = Modifier.heightIn(min = 16.dp))
                Button(
                    onClick = {
                        if (driver == null) {
                            viewModel.setError("Драйвер не выбран")
                            return@Button
                        }
                        if (jdbcUrl.isBlank()) {
                            viewModel.setError("URL не может быть пустым")
                            return@Button
                        }
                        if (driver == Utils.POSTGRES_DRIVER && (user.isBlank() || password.isBlank())) {
                            viewModel.setError("Пользователь и пароль не могут быть пустыми для PostgreSQL")
                            return@Button
                        }

                        onConnect(jdbcUrl, driver!!, user, password)
                    },
                    contentPadding = ButtonDefaults.LargeContentPadding,
                ) {
                    Text(
                        "Подключиться",
                        style = ButtonDefaults.textStyleFor(ButtonDefaults.LargeContainerHeight)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DriverDropDown(
    selectedItem: String?,
    onItemSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = selectedItem.orEmpty(),
            onValueChange = {},
            readOnly = true,
            label = { Text(stringResource(Res.string.input_driver)) },
            supportingText = { Text(stringResource(Res.string.input_supporting_required)) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = modifier.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable),
            shape = RoundedCornerShape(16.dp),
            singleLine = true
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                text = { Text("H2") },
                onClick = {
                    onItemSelected(Utils.H2_DRIVER)
                    expanded = false
                }
            )
            DropdownMenuItem(
                text = { Text("Postgres") },
                onClick = {
                    onItemSelected(Utils.POSTGRES_DRIVER)
                    expanded = false
                }
            )
        }
    }
}