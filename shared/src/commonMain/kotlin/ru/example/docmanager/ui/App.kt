/*
 * Copyright (c) 2026 Andrew Z.
 * docmanager — a scalable document management system.
 */

package ru.example.docmanager.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import ru.example.docmanager.ui.navigation.Screen
import ru.example.docmanager.ui.navigation.navigationConfig
import ru.example.docmanager.ui.navigation.navigationTransition
import ru.example.docmanager.ui.navigation.popTransition
import ru.example.docmanager.ui.screens.*
import ru.example.docmanager.viewmodel.AppViewModel
import ru.example.docmanager.viewmodel.ConnectionState

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun App(
    darkTheme: Boolean = isSystemInDarkTheme()
) {
    val viewModel = koinInject<AppViewModel>()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val backStack = rememberNavBackStack(
        navigationConfig,
        Screen.Connection
    )

    LaunchedEffect(state) {
        if (state is ConnectionState.Connected) {
            backStack.clear()
            backStack.add(Screen.Start)
        }
    }

    val currentTitleResource by remember(backStack) {
        derivedStateOf {
            (backStack.lastOrNull() as? Screen)?.stringResource
        }
    }

    val canNavigateBack = backStack.size > 1

    val onBack: () -> Unit = remember(backStack) {
        {
            if (backStack.size > 1)
                backStack.removeLastOrNull()
        }
    }

    AppTheme(
        darkTheme = darkTheme
    ) {
        Scaffold (
            topBar = {
                AnimatedVisibility(currentTitleResource != null) {
                    TopAppBar(
                        title = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                AnimatedVisibility(
                                    visible = currentTitleResource != null,
                                    enter = fadeIn(),
                                    exit = fadeOut()
                                ) {
                                    currentTitleResource?.let {
                                        Text(stringResource(it))
                                    }
                                }
                            }
                        },
                        navigationIcon = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                AnimatedVisibility(visible = canNavigateBack) {
                                    IconButton(onClick = onBack) {
                                        Icon(
                                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                                            contentDescription = "Back",
                                        )
                                    }
                                }
                            }
                        }
                    )
                }
            },
            snackbarHost = {
                SnackbarHost(
                    hostState = snackbarHostState,
                    snackbar = {
                        Snackbar(
                            snackbarData = it,
                            shape = MaterialTheme.shapes.large,
                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    }
                )
            }
        ) { paddingValues ->
            Column (
                modifier = Modifier.fillMaxSize()
                    .padding(paddingValues)
            ) {
                NavDisplay(
                    backStack = backStack,
                    onBack = onBack,
                    transitionSpec = navigationTransition,
                    popTransitionSpec = popTransition,
                    entryProvider = entryProvider {
                        entry <Screen.Connection> {
                            when (state) {
                                ConnectionState.Disconnected,
                                is ConnectionState.Error -> {
                                    ConnectionScreen(
                                        state = state,
                                        onConnect = viewModel::connect,
                                        onError = viewModel::error
                                    )
                                }
                                else -> {
                                    Box (
                                        modifier = Modifier.fillMaxSize()
                                            .background(MaterialTheme.colorScheme.background),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        ContainedLoadingIndicator(modifier = Modifier.size(128.dp))
                                    }
                                }
                            }
                        }
                        entry <Screen.Start> {
                            StartScreen (
                                onDisconnect = viewModel::disconnect,
                                navigateTo = { backStack.add(it) }
                            )
                        }
                        entry <Screen.Settings> {
                            SettingsScreen()
                        }
                        entry <Screen.Documents> {
                            DocumentsScreen(navigateTo = { backStack.add(it) })
                        }
                        entry <Screen.References> {
                            ReferencesScreen(navigateTo = { backStack.add(it) })
                        }
                        entry <Screen.Document> {
                            DocumentScreen(it)
                        }
                        entry <Screen.Reference> {
                            ReferenceScreen(
                                destination = it,
                                onStatusMessage = {
                                    scope.launch {
                                        snackbarHostState.showSnackbar(it)
                                    }
                                }
                            )
                        }
                    }
                )
            }
        }
    }
}