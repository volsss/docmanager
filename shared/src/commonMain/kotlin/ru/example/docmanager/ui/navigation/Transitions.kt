/*
 * Copyright (c) 2026 Andrew Z.
 * docmanager — a scalable document management system.
 */

package ru.example.docmanager.ui.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.scene.Scene
import androidx.navigationevent.NavigationEvent
import androidx.navigationevent.NavigationEvent.SwipeEdge

val predictiveTransition : AnimatedContentTransitionScope<Scene<NavKey>>.(
    @SwipeEdge Int
) -> ContentTransform = { edge ->
    ContentTransform(
        targetContentEnter = scaleIn(
            initialScale = 0.95f,
            animationSpec = tween(220, easing = FastOutSlowInEasing)
        ) + fadeIn(
            animationSpec = tween(220, easing = FastOutSlowInEasing)
        ),

        initialContentExit = slideOutOfContainer(
            towards = AnimatedContentTransitionScope.SlideDirection.Start,
            animationSpec = tween(220, easing = FastOutSlowInEasing),
            targetOffset = { if (edge == NavigationEvent.EDGE_RIGHT) it/7 else -it/7 }
        ) + scaleOut(
            targetScale = 0.75f,
            animationSpec = tween(220, easing = FastOutSlowInEasing)
        ) + fadeOut(
            animationSpec = tween(280, easing = FastOutSlowInEasing)
        )
    )
}

val navigationTransition : AnimatedContentTransitionScope<Scene<NavKey>>.() -> ContentTransform = {
    val isTargetDetail = targetState.key is Screen.Document ||
            targetState.key is Screen.Reference ||
            targetState.key is Screen.Settings

    if (isTargetDetail) {
        ContentTransform(
            targetContentEnter = slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Start,
                animationSpec = tween(280, easing = FastOutSlowInEasing),
                initialOffset = { it / 5 }
            ) + fadeIn(
                animationSpec = tween(280, easing = FastOutSlowInEasing)
            ),

            initialContentExit = slideOutOfContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Start,
                animationSpec = tween(240, easing = FastOutSlowInEasing),
                targetOffset = { -it / 5 }
            ) + fadeOut(
                animationSpec = tween(200, easing = FastOutSlowInEasing)
            )
        )
    } else {
        ContentTransform(
            targetContentEnter = fadeIn(
                animationSpec = tween(200, easing = FastOutSlowInEasing)
            ) + scaleIn(
                initialScale = 0.9f,
                animationSpec = tween(220, easing = FastOutSlowInEasing)
            ),

            initialContentExit = fadeOut(
                animationSpec = tween(180, easing = FastOutSlowInEasing)
            )
        )
    }
}

val popTransition : AnimatedContentTransitionScope<Scene<NavKey>>.() -> ContentTransform = {
    ContentTransform(
        targetContentEnter = slideInVertically(
            initialOffsetY = { it / 2 },
            animationSpec = tween(240, easing = FastOutSlowInEasing)
        ) + scaleIn(
            initialScale = 0.75f,
            animationSpec = tween(220, easing = FastOutSlowInEasing)
        ) + fadeIn(
            animationSpec = tween(280, easing = FastOutSlowInEasing)
        ),

        initialContentExit = slideOutVertically(
            targetOffsetY = { it / 2 },
            animationSpec = tween(280, easing = FastOutSlowInEasing)
        ) + scaleOut(
            targetScale = 0.75f,
            animationSpec = tween(220, easing = FastOutSlowInEasing)
        ) + fadeOut(
            animationSpec = tween(240, easing = FastOutSlowInEasing)
        )
    )
}