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
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
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
    val bottomBarRoutes = listOf(
        Route.Start,
        Route.DocumentTypes,
        Route.References
    )
    val showBottomBar = bottomBarRoutes.contains(currentRoute)

    Box(
        modifier = modifier
            .background(MaterialTheme.colorScheme.surfaceDim)
            .fillMaxSize(),
    ) {
        RouterNavDisplay(
            backStack = backStack,
            navigateTo = navigateTo,
            onBack = onBack,
            modifier = if (showBottomBar) Modifier.fillMaxSize().padding(bottom = 80.dp) else Modifier.fillMaxSize()
        )

        RouterBottomBar(
            currentRoute = currentRoute,
            routes = bottomBarRoutes,
            navigateTo = navigateTo,
            modifier = Modifier.align(Alignment.BottomCenter)
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
    val canGoBack = backStack.size > 1
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
                    },
                    canGoBack = false,
                    onBack = onBack
                )
            }
            entry<Route.DocumentTypes> {
                DocumentTypesScreen(
                    navigateTo = { route ->
                        navigateTo(route, NavigationType.PUSH)
                    },
                    canGoBack = false,
                    onBack = onBack
                )
            }
            entry<Route.References> {
                ReferencesScreen(
                    navigateTo = { route ->
                        navigateTo(route, NavigationType.PUSH)
                    },
                    canGoBack = false,
                    onBack = onBack
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
                    },
                    canGoBack = canGoBack,
                    onBack = onBack
                )
            }
            entry<Route.Form> {
                FormScreen(
                    destination = it,
                    navigateTo = navigateTo,
                    canGoBack = canGoBack,
                    onBack = onBack
                )
            }
            entry<Route.Reference> {
                ReferenceScreen(
                    destination = it,
                    viewModel = koinViewModel(key = it.type.name) {
                        parametersOf(it)
                    },
                    canGoBack = canGoBack,
                    onBack = onBack
                )
            }
            entry<Route.Settings> {
                SettingsScreen(
                    canGoBack = canGoBack,
                    onBack = onBack
                )
            }
        }
    )
}


@Composable
private fun RouterBottomBar(
    currentRoute: Route,
    routes: List<Route>,
    navigateTo: (Route, NavigationType) -> Unit,
    modifier: Modifier = Modifier,
) {
    AnimatedVisibility(
        visible = routes.contains(currentRoute),
        modifier = modifier,
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
            routes.forEach { route ->
                RouterBarItem(
                    route = route,
                    selected = currentRoute == route,
                    onClick = { navigateTo(route, NavigationType.RESET) },
                )
            }
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