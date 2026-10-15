/*
 * Copyright (c) 2026 Andrew Z.
 * docmanager — a scalable document management system.
 */

package ru.example.docmanager.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
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
import org.koin.compose.koinInject
import ru.example.docmanager.ui.navigation.*
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
                    predictivePopTransitionSpec = predictiveTransition,
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
                            SettingsScreen(
                                onBack = onBack
                            )
                        }
                        entry <Screen.DocumentTypes> {
                            DocumentTypesScreen(
                                navigateTo = { backStack.add(it) },
                                onBack = onBack
                            )
                        }
                        entry <Screen.Documents> {
                            DocumentsScreen(
                                destination = it,
                                navigateTo = { backStack.add(it) },
                                onBack = onBack
                            )
                        }
                        entry <Screen.Document> {
                            DocumentScreen(
                                destination = it,
                                onBack = onBack
                            )
                        }
                        entry <Screen.References> {
                            ReferencesScreen(
                                navigateTo = { backStack.add(it) },
                                onBack = onBack
                            )
                        }
                        entry <Screen.Reference> {
                            ReferenceScreen(
                                destination = it,
                                onBack = onBack,
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