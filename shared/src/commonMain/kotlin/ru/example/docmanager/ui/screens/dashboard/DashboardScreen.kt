/*
 * Copyright (c) 2026 Andrew Z.
 * docmanager — a scalable document management system.
 */

package ru.example.docmanager.ui.screens.dashboard

import androidx.compose.animation.*
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.scene.Scene
import androidx.navigation3.ui.NavDisplay
import androidx.savedstate.serialization.SavedStateConfiguration
import docmanager.shared.generated.resources.Res
import docmanager.shared.generated.resources.start_title
import kotlinx.coroutines.launch
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import org.jetbrains.compose.resources.stringResource
import ru.example.docmanager.ui.screens.dashboard.tab.*
import ru.example.docmanager.viewmodel.DashboardDestination

private val config = SavedStateConfiguration {
    serializersModule = SerializersModule {
        polymorphic(NavKey::class) {
            subclass(
                DashboardDestination.Documents::class,
                DashboardDestination.Documents.serializer()
            )
            subclass(
                DashboardDestination.References::class,
                DashboardDestination.References.serializer()
            )
            subclass(
                DashboardDestination.Settings::class,
                DashboardDestination.Settings.serializer()
            )
            subclass(
                DashboardDestination.Document::class,
                DashboardDestination.Document.serializer()
            )
            subclass(
                DashboardDestination.Reference::class,
                DashboardDestination.Reference.serializer()
            )
        }
    }
}

@Composable
fun DashboardScreen(
    onDisconnect: () -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val backStack = rememberNavBackStack(
        config,
        DashboardDestination.Documents
    )
    val current by remember {
        derivedStateOf {
            backStack.last() as DashboardDestination
        }
    }
    val title by remember {
        derivedStateOf {
            (backStack.last() as DashboardDestination).stringResource
        }
    }
    val scope = rememberCoroutineScope()
    val canNavigateBack = backStack.size > 1

    Scaffold(
        topBar = {
            DashboardTopBar(
                title = stringResource(title),
                canNavigateBack = canNavigateBack,
                onBack = {
                    if (backStack.size > 1) {
                        backStack.removeLastOrNull()
                    }
                },
                onSettings = {
                    if (backStack.lastOrNull() != DashboardDestination.Settings) {
                        backStack.add(DashboardDestination.Settings)
                    }
                },
                onDisconnect = onDisconnect,
            )
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
        },
        containerColor = MaterialTheme.colorScheme.primaryContainer,
        floatingActionButton = {
            AnimatedVisibility(
                visible = current is DashboardDestination.Reference,
                enter = scaleIn() + fadeIn(),
                exit = scaleOut() + fadeOut()
            ) {
                FloatingActionButton(
                    onClick = {

                    }
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Add,
                        contentDescription = null
                    )
                }
            }
        }
    ) { paddingValues ->
        DashboardContent(
            backStack = backStack,
            modifier = Modifier.padding(paddingValues),
            onStatusMessage = {
                scope.launch {
                    snackbarHostState.showSnackbar(it)
                }
            }
        )
    }
}

private val navigationTransition : AnimatedContentTransitionScope<Scene<NavKey>>.() -> ContentTransform = {
    val isTargetDetail = targetState.key is DashboardDestination.Document ||
            targetState.key is DashboardDestination.Reference ||
            targetState.key is DashboardDestination.Settings

    if (isTargetDetail) {
        (slideIntoContainer(
            towards = AnimatedContentTransitionScope.SlideDirection.Start,
            animationSpec = tween(280, easing = FastOutSlowInEasing),
            initialOffset = { it / 5 }
        ) + fadeIn(
            animationSpec = tween(280, easing = FastOutSlowInEasing)
        )).togetherWith(
            slideOutOfContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Start,
                animationSpec = tween(240, easing = FastOutSlowInEasing),
                targetOffset = { -it / 5 }
            ) + fadeOut(
                animationSpec = tween(200, easing = FastOutSlowInEasing)
            )
        )
    } else {
        (fadeIn(
            animationSpec = tween(220, easing = FastOutSlowInEasing)
        ) + scaleIn(
            initialScale = 0.96f,
            animationSpec = tween(220, easing = FastOutSlowInEasing)
        )).togetherWith(
            fadeOut(
                animationSpec = tween(180, easing = FastOutSlowInEasing)
            ) + scaleOut(
                targetScale = 0.96f,
                animationSpec = tween(180, easing = FastOutSlowInEasing)
            )
        )
    }
}

private val popTransition : AnimatedContentTransitionScope<Scene<NavKey>>.() -> ContentTransform = {
    (slideIntoContainer(
        towards = AnimatedContentTransitionScope.SlideDirection.End,
        animationSpec = tween(280, easing = FastOutSlowInEasing),
        initialOffset = { -it / 5 }
    ) + fadeIn(
        animationSpec = tween(280, easing = FastOutSlowInEasing)
    )).togetherWith(
        slideOutOfContainer(
            towards = AnimatedContentTransitionScope.SlideDirection.End,
            animationSpec = tween(240, easing = FastOutSlowInEasing),
            targetOffset = { it / 5 }
        ) + fadeOut(
            animationSpec = tween(200, easing = FastOutSlowInEasing)
        )
    )
}

@Composable
fun DashboardContent (
    backStack: NavBackStack<NavKey>,
    onStatusMessage: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val currentDestination = backStack.lastOrNull() as? DashboardDestination
        ?: DashboardDestination.Documents

    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        DashboardTabs(
            destination = currentDestination,
            onTabSelected = { targetDestination ->
                val current = backStack.lastOrNull() as? DashboardDestination
                if (current == targetDestination) return@DashboardTabs

                while (backStack.isNotEmpty()) backStack.removeLastOrNull()
                backStack.add(targetDestination)
            }
        )
        NavDisplay(
            backStack = backStack,
            onBack = {
                if (backStack.size > 1) backStack.removeLastOrNull()
            },
            transitionSpec = navigationTransition,
            popTransitionSpec = popTransition,
            entryProvider = entryProvider {
                entry <DashboardDestination.Settings> {
                    SettingsTab()
                }
                entry <DashboardDestination.Documents> {
                    DocumentsTab(navigateTo = { backStack.add(it) })
                }
                entry <DashboardDestination.References> {
                    ReferencesTab(navigateTo = { backStack.add(it) })
                }
                entry <DashboardDestination.Document> {
                    DocumentTab(it)
                }
                entry <DashboardDestination.Reference> {
                    ReferenceTab(
                        destination = it,
                        onStatusMessage = onStatusMessage
                    )
                }
            }
        )
    }
}