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

package ru.example.docmanager.ui.router

import androidx.compose.animation.*
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import docmanager.shared.generated.resources.Res
import docmanager.shared.generated.resources.component_back_button
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import ru.example.docmanager.ui.screens.*
import ru.example.docmanager.ui.screens.forms.FormScreen

@Composable
fun Router(
    modifier: Modifier = Modifier
) {
    var onFabClickAction by remember {
        mutableStateOf<(() -> Unit)?>(null)
    }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val backStack = rememberNavBackStack(
        configuration = routerConfig,
        Route.Start
    )

    val currentRoute = backStack.last() as Route

    fun onBack() {
        if (backStack.size > 1) {
            backStack.removeLastOrNull()
            onFabClickAction = null
        }
    }

    fun navigateToRoot(route: Route) {
        if (backStack.last() != route) {
            backStack.clear()
            backStack.add(route)
        }
    }

    fun showSnackbar(message: String) {
        scope.launch {
            snackbarHostState.showSnackbar(message)
        }
    }

    RouterContent(
        currentRoute = currentRoute,
        backStack = backStack,
        onBack = ::onBack,
        navigateToRoot = ::navigateToRoot,
        showSnackbar = ::showSnackbar,
        registerFabClickAction = { action ->
            onFabClickAction = action
        },
        snackbarHostState = snackbarHostState,
        modifier = modifier,
        onFabClickAction = onFabClickAction,
    )
}

@Composable
private fun RouterContent(
    currentRoute: Route,
    backStack: NavBackStack<NavKey>,
    onBack: () -> Unit,
    navigateToRoot: (Route) -> Unit,
    showSnackbar: (String) -> Unit,
    registerFabClickAction: ((() -> Unit)?) -> Unit,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
    onFabClickAction: (() -> Unit)? = null,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            RouterTopBar(
                currentRoute = currentRoute,
                canGoBack = backStack.size > 1,
                onBack = onBack
            )
        },
        snackbarHost = {
            RouterSnackbarHost(snackbarHostState)
        },
        bottomBar = {
            RouterBottomBar(
                currentRoute = currentRoute,
                navigateToRoot = navigateToRoot
            )
        },
        floatingActionButton = {
            RouterFloatingActionButton(
                currentRoute = currentRoute,
                onFabClickAction = onFabClickAction
            )
        }
    ) { paddingValues ->
        RouterNavDisplay(
            backStack = backStack,
            onBack = onBack,
            registerFabClickAction = registerFabClickAction,
            showSnackbar = showSnackbar,
            modifier = Modifier.padding(paddingValues)
        )
    }
}

@Composable
private fun RouterSnackbarHost(
    snackbarHostState: SnackbarHostState
) {
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

@Composable
private fun RouterNavDisplay(
    backStack: NavBackStack<NavKey>,
    onBack: () -> Unit,
    registerFabClickAction: ((() -> Unit)?) -> Unit,
    showSnackbar: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    NavDisplay(
        backStack = backStack,
        onBack = onBack,
        transitionSpec = navigationTransition,
        popTransitionSpec = popTransition,
        predictivePopTransitionSpec = predictiveTransition,
        modifier = modifier.fillMaxSize(),
        entryProvider = entryProvider {
            entry<Route.Start> {
                StartScreen(
                    navigateTo = backStack::add
                )
            }
            entry<Route.Settings> {
                SettingsScreen()
            }
            entry<Route.DocumentTypes> {
                DocumentTypesScreen(
                    navigateTo = backStack::add
                )
            }
            entry<Route.Documents> {
                DocumentsScreen(
                    destination = it,
                    navigateTo = backStack::add,
                    registerFabClickAction = registerFabClickAction,
                    viewModel = koinViewModel(key = it.type.name) {
                        parametersOf(it)
                    }
                )
            }
            entry<Route.Form> {
                FormScreen(
                    destination = it
                )
            }
            entry<Route.References> {
                ReferencesScreen(
                    navigateTo = backStack::add
                )
            }
            entry<Route.Reference> {
                ReferenceScreen(
                    destination = it,
                    onStatusMessage = showSnackbar,
                    viewModel = koinViewModel(key = it.type.name) {
                        parametersOf(it)
                    }
                )
            }
        }
    )
}

@Composable
private fun RouterTopBar(
    currentRoute: Route,
    canGoBack: Boolean,
    onBack: () -> Unit
) {
    TopAppBar(
        navigationIcon = {
            AnimatedVisibility(
                visible = canGoBack,
                enter = expandHorizontally(),
                exit = shrinkHorizontally()
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                        contentDescription = stringResource(Res.string.component_back_button),
                    )
                }
            }
        },
        title = {
            Text(
                text = when (currentRoute) {
                    is Route.Reference -> {
                        stringResource(currentRoute.type.stringResource)
                    }

                    is Route.Documents -> {
                        stringResource(currentRoute.type.stringResource)
                    }

                    is Route.Form -> {
                        stringResource(currentRoute.type.stringResource)
                    }

                    else -> stringResource(currentRoute.stringResource)
                }
            )
        }
    )
}

@Composable
private fun RouterBottomBar(
    currentRoute: Route,
    navigateToRoot: (Route) -> Unit
) {
    AnimatedVisibility(
        visible = currentRoute is Route.Start
                || currentRoute is Route.Settings
                || currentRoute is Route.DocumentTypes
                || currentRoute is Route.References,
        enter = slideInVertically(
            initialOffsetY = { fullHeight -> fullHeight },
        ) + expandVertically(
            expandFrom = Alignment.Bottom
        ) + fadeIn(),
        exit = slideOutVertically(
            targetOffsetY = { fullHeight -> fullHeight },
        ) + shrinkVertically(
            shrinkTowards = Alignment.Bottom
        ) + fadeOut()
    ) {
        NavigationBar {
            RouterBarItem(
                route = Route.Start,
                selected = currentRoute is Route.Start,
                onClick = { navigateToRoot(Route.Start) },
            )
            RouterBarItem(
                route = Route.DocumentTypes,
                selected = currentRoute is Route.DocumentTypes,
                onClick = { navigateToRoot(Route.DocumentTypes) },
            )
            RouterBarItem(
                route = Route.References,
                selected = currentRoute is Route.References,
                onClick = { navigateToRoot(Route.References) },
            )
            RouterBarItem(
                route = Route.Settings,
                selected = currentRoute is Route.Settings,
                onClick = { navigateToRoot(Route.Settings) },
            )
        }
    }
}

@Composable
private fun RowScope.RouterBarItem(
    route: Route,
    selected: Boolean,
    onClick: () -> Unit,
) {
    NavigationBarItem(
        selected = selected,
        onClick = onClick,
        icon = {
            Icon(
                imageVector = route.icon,
                contentDescription = stringResource(route.stringResource),
            )
        },
        colors = NavigationBarItemDefaults.colors(
            selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
            selectedTextColor = MaterialTheme.colorScheme.onPrimaryContainer,
            indicatorColor = MaterialTheme.colorScheme.primaryContainer,
        ),
    )
}

@Composable
private fun RouterFloatingActionButton(
    currentRoute: Route,
    onFabClickAction: (() -> Unit)? = null,
) {
    FloatingActionButton(
        onClick = { onFabClickAction?.invoke() },
        modifier = Modifier.animateFloatingActionButton(
            visible = currentRoute is Route.Documents
                    && onFabClickAction != null,
            alignment = Alignment.BottomEnd,
        ),
    ) {
        Icon(Icons.Rounded.Add, contentDescription = null)
    }
}
