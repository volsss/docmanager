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

val navigationTransition : AnimatedContentTransitionScope<Scene<NavKey>>.() -> ContentTransform = {
    val isTargetDetail = targetState.key is Screen.Document ||
            targetState.key is Screen.Reference ||
            targetState.key is Screen.Settings

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

val popTransition : AnimatedContentTransitionScope<Scene<NavKey>>.() -> ContentTransform = {
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