/*
 * Copyright (c) 2026 Andrew Z.
 * docmanager — a scalable document management system.
 */

package ru.example.docmanager.ui.screens.dashboard

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import androidx.savedstate.serialization.SavedStateConfiguration
import docmanager.shared.generated.resources.Res
import docmanager.shared.generated.resources.dashboard_project_name_fallback
import kotlinx.coroutines.launch
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import ru.example.docmanager.ui.screens.dashboard.tab.*
import ru.example.docmanager.viewmodel.DashboardDestination
import ru.example.docmanager.viewmodel.SettingsViewModel

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
    val settingsViewModel = koinInject<SettingsViewModel>()
    val snackbarHostState = remember { SnackbarHostState() }
    val backStack = rememberNavBackStack(
        config,
        DashboardDestination.Documents
    )
    val projectMetadata by settingsViewModel.metadata.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()
    val canNavigateBack = backStack.size > 1

    Scaffold(
        topBar = {
            DashboardTopBar(
                title = projectMetadata?.name ?: stringResource(Res.string.dashboard_project_name_fallback),
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
        containerColor = MaterialTheme.colorScheme.primaryContainer
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
                if (backStack.size > 1) {
                    backStack.removeLastOrNull()
                }
            },
            transitionSpec = {
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
            },
            popTransitionSpec = {
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
            },
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