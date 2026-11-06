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
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import docmanager.shared.generated.resources.Res
import docmanager.shared.generated.resources.component_back_button
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import ru.example.docmanager.ui.screens.*
import ru.example.docmanager.ui.screens.forms.FormScreen

@Composable
fun Router(
    modifier: Modifier = Modifier
) {
    val backStack = rememberNavBackStack(
        configuration = routerConfig,
        Route.Start
    )

    val currentRoute = backStack.last() as Route

    fun onBack() {
        if (backStack.size > 1) {
            backStack.removeLastOrNull()
        }
    }

    fun navigateTo(
        route: Route,
        navigationType: NavigationType = NavigationType.PUSH
    ) {
        when (navigationType) {
            NavigationType.PUSH -> Unit
            NavigationType.RESET -> backStack.clear()
            NavigationType.REPLACE -> backStack.removeLastOrNull()
        }
        backStack.add(route)
    }

    RouterContent(
        currentRoute = currentRoute,
        backStack = backStack,
        onBack = ::onBack,
        navigateTo = ::navigateTo,
        modifier = modifier,
    )
}

@Composable
private fun RouterContent(
    currentRoute: Route,
    backStack: NavBackStack<NavKey>,
    onBack: () -> Unit,
    navigateTo: (Route, NavigationType) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            RouterTopBar(
                currentRoute = currentRoute,
                canGoBack = backStack.size > 1,
                onBack = onBack
            )
        },
        bottomBar = {
            RouterBottomBar(
                currentRoute = currentRoute,
                navigateTo = navigateTo
            )
        }
    ) { paddingValues ->
        RouterNavDisplay(
            backStack = backStack,
            navigateTo = navigateTo,
            onBack = onBack,
            modifier = Modifier.padding(paddingValues)
        )
    }
}

@Composable
private fun RouterNavDisplay(
    backStack: NavBackStack<NavKey>,
    navigateTo: (Route, NavigationType) -> Unit,
    onBack: () -> Unit,
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
                    navigateTo = { route ->
                        navigateTo(route, NavigationType.PUSH)
                    }
                )
            }
            entry<Route.Settings> {
                SettingsScreen()
            }
            entry<Route.DocumentTypes> {
                DocumentTypesScreen(
                    navigateTo = { route ->
                        navigateTo(route, NavigationType.PUSH)
                    }
                )
            }
            entry<Route.Documents> {
                DocumentsScreen(
                    destination = it,
                    navigateTo = { route ->
                        navigateTo(route, NavigationType.PUSH)
                    },
                    viewModel = koinViewModel(key = it.type.name) {
                        parametersOf(it)
                    }
                )
            }
            entry<Route.Form> {
                FormScreen(
                    destination = it,
                    navigateTo = navigateTo
                )
            }
            entry<Route.References> {
                ReferencesScreen(
                    navigateTo = { route ->
                        navigateTo(route, NavigationType.PUSH)
                    }
                )
            }
            entry<Route.Reference> {
                ReferenceScreen(
                    destination = it,
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
    navigateTo: (Route, NavigationType) -> Unit
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
                onClick = { navigateTo(Route.Start, NavigationType.RESET) },
            )
            RouterBarItem(
                route = Route.DocumentTypes,
                selected = currentRoute is Route.DocumentTypes,
                onClick = { navigateTo(Route.DocumentTypes, NavigationType.RESET) },
            )
            RouterBarItem(
                route = Route.References,
                selected = currentRoute is Route.References,
                onClick = { navigateTo(Route.References, NavigationType.RESET) },
            )
            RouterBarItem(
                route = Route.Settings,
                selected = currentRoute is Route.Settings,
                onClick = { navigateTo(Route.Settings, NavigationType.RESET) },
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
