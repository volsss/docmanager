package tech.ilug.documentmanager.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import tech.ilug.documentmanager.di.Platform
import tech.ilug.documentmanager.di.getPlatform
import tech.ilug.documentmanager.viewmodel.ConnectionViewModel

object DefaultValues {
    const val JVM_JDBC = "jdbc:h2:~/documents;MODE=MySQL"
    const val ANDROID_JDBC = "jdbc:h2:/data/data/tech.ilug.documentmanager/documents;MODE=MySQL"
    const val POSTGRES_JDBC = "jdbc:postgresql://localhost:5432/documents"
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ConnectionScreen(
    viewModel: ConnectionViewModel,
    onConnect: (String, String, String, String) -> Unit
) {
    val platform = remember { getPlatform() }
    var jdbcUrl by remember { mutableStateOf("") }
    var driver by remember { mutableStateOf<String?>(null) }
    var user by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val error by viewModel.error.collectAsState()

    Scaffold(
        topBar = { TopAppBar(title = { Text("Подключение к базе данных") }) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Column(
                modifier = Modifier.widthIn(max = 500.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = jdbcUrl,
                    onValueChange = { jdbcUrl = it },
                    label = { Text("JDBC URL*") },
                    supportingText = { Text("*обязательно для заполнения") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                DriverDropDown(
                    modifier = Modifier.fillMaxWidth(),
                    selectedItem = driver,
                    onItemSelected = {
                        driver = it
                        if (driver == "org.postgresql.Driver")
                            jdbcUrl = DefaultValues.POSTGRES_JDBC
                        if (driver == "org.h2.Driver")
                            jdbcUrl = if (platform == Platform.JVM) DefaultValues.JVM_JDBC
                            else DefaultValues.ANDROID_JDBC
                    }
                )

                OutlinedTextField(
                    value = user,
                    onValueChange = { user = it },
                    label = { Text("Пользователь") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Пароль") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    visualTransformation = PasswordVisualTransformation()
                )

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
                        if (driver == "org.postgresql.Driver" && (user.isBlank() || password.isBlank())) {
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
            label = { Text("Драйвер*") },
            supportingText = { Text("*обязательно для заполнения") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = modifier.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable),
            singleLine = true
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                text = { Text("H2") },
                onClick = {
                    onItemSelected("org.h2.Driver")
                    expanded = false
                }
            )
            DropdownMenuItem(
                text = { Text("PostgreSQL") },
                onClick = {
                    onItemSelected("org.postgresql.Driver")
                    expanded = false
                }
            )
        }
    }
}