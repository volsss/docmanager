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
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.FormatColorReset
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import tech.ilug.documentmanager.di.Platform
import tech.ilug.documentmanager.di.getPlatform
import tech.ilug.documentmanager.viewmodel.ConnectionViewModel

object DefaultValues {
    object JVM {
        const val jdbcUrl = "jdbc:h2:~/documents;MODE=MySQL"
        const val driver = "org.h2.Driver"
    }
    object Android {
        const val jdbcUrl = "jdbc:h2:/data/data/tech.ilug.documentmanager/documents;MODE=MySQL"
        const val driver = "org.h2.Driver"
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ConnectionScreen (
    viewModel: ConnectionViewModel,
    onConnect: (String, String, String, String) -> Unit
) {
    val platform = remember { getPlatform() }
    var jdbcUrl by remember { mutableStateOf(
        if(platform == Platform.JVM) DefaultValues.JVM.jdbcUrl
        else DefaultValues.Android.jdbcUrl
    ) }
    var driver by remember { mutableStateOf(
        if(platform == Platform.JVM) DefaultValues.JVM.driver
        else DefaultValues.Android.driver
    ) }
    var user by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val error by viewModel.error.collectAsState()

    Scaffold (
        topBar = {
            TopAppBar(title = { Text("Документооборот") })
        }
    ) { paddingValues ->
        Column (
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                "Подключение к базе данных",
                style = MaterialTheme.typography.headlineSmallEmphasized,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(32.dp))
            OutlinedTextField(
                value = jdbcUrl,
                onValueChange = { jdbcUrl = it },
                label = { Text("JDBC URL*") },
                supportingText = { Text("*обязательно для заполнения") },
                trailingIcon = {
                    IconButton(
                        onClick = {
                            jdbcUrl = if(platform == Platform.JVM) DefaultValues.JVM.jdbcUrl
                            else DefaultValues.Android.jdbcUrl
                        }
                    ) {
                        Icon(Icons.Default.Refresh, "Сбросить")
                    }
                },
                singleLine = true,
                modifier = Modifier.widthIn(500.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = driver,
                onValueChange = { driver = it },
                label = { Text("Драйвер*") },
                supportingText = { Text("*обязательно для заполнения") },
                trailingIcon = {
                    IconButton(
                        onClick = {
                            driver = if(platform == Platform.JVM) DefaultValues.JVM.driver
                            else DefaultValues.Android.driver
                        }
                    ) {
                        Icon(Icons.Default.Refresh, "Сбросить")
                    }
                },
                singleLine = true,
                modifier = Modifier.widthIn(500.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = user,
                onValueChange = { user = it },
                label = { Text("Пользователь") },
                singleLine = true,
                modifier = Modifier.widthIn(500.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Пароль") },
                singleLine = true,
                modifier = Modifier.widthIn(500.dp),
                visualTransformation = PasswordVisualTransformation()
            )

            AnimatedVisibility(error != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text (
                    error!!,
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            Button (
                modifier = Modifier
                    .heightIn(ButtonDefaults.MediumContainerHeight),
                onClick = { onConnect(jdbcUrl, driver, user, password) }
            ) {
                Text (
                    "Подключиться",
                    modifier = Modifier.padding(horizontal = 24.dp),
                    style = ButtonDefaults.textStyleFor (
                        ButtonDefaults.MediumContainerHeight
                    )
                )
            }
        }
    }
}