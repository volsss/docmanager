/*
 * Copyright (c) 2026 Andrew Z.
 * docmanager — a scalable document management system.
 */

package ru.example.docmanager.ui.router

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
            entry<Route.Document> {
                DocumentScreen(
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
                    onStatusMessage = showSnackbar
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
            if (canGoBack) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                        contentDescription = stringResource(Res.string.component_back_button),
                    )
                }
            }
        },
        title = {
            Text(text = stringResource(currentRoute.stringResource))
        }
    )
}

@Composable
private fun RouterBottomBar(
    currentRoute: Route,
    navigateToRoot: (Route) -> Unit
) {
    NavigationBar {
        RouterBarItem(
            route = Route.Start,
            selected = currentRoute is Route.Start,
            onClick = { navigateToRoot(Route.Start) },
        )
        RouterBarItem(
            route = Route.DocumentTypes,
            selected = currentRoute is Route.DocumentTypes ||
                    currentRoute is Route.Documents ||
                    currentRoute is Route.Document,
            onClick = { navigateToRoot(Route.DocumentTypes) },
        )
        RouterBarItem(
            route = Route.References,
            selected = currentRoute is Route.References ||
                    currentRoute is Route.Reference,
            onClick = { navigateToRoot(Route.References) },
        )
        RouterBarItem(
            route = Route.Settings,
            selected = currentRoute is Route.Settings,
            onClick = { navigateToRoot(Route.Settings) },
        )
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
    AnimatedVisibility(
        visible = currentRoute is Route.Documents
                && onFabClickAction != null,
        enter = slideInVertically { it } + fadeIn(),
        exit = slideOutVertically { it } + fadeOut(),
    ) {
        FloatingActionButton(onClick = { onFabClickAction?.invoke() }) {
            Icon(Icons.Rounded.Add, contentDescription = null)
        }
    }
}
