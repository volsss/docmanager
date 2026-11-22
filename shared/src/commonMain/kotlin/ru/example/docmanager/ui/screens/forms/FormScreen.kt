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

package ru.example.docmanager.ui.screens.forms

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import ru.example.docmanager.domain.document.DocumentType
import ru.example.docmanager.ui.router.NavigationType
import ru.example.docmanager.ui.router.Route
import ru.example.docmanager.ui.screens.forms.powerofattorney.PowerOfAttorneyScreen

@Composable
fun FormScreen(
    destination: Route.Form,
    navigateTo: (Route, NavigationType) -> Unit,
    canGoBack: Boolean,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val type = destination.type
    val document = destination.document

    when (type) {
        DocumentType.POWER_OF_ATTORNEY -> PowerOfAttorneyScreen(
            document = document,
            canGoBack = canGoBack,
            onBack = onBack,
            navigateTo = navigateTo,
            modifier = modifier
        )

        // DocumentType.TEST -> TestScreen(...)
    }
}