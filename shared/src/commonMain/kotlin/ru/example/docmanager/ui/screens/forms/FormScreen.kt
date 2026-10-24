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

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import docmanager.shared.generated.resources.Res
import docmanager.shared.generated.resources.document_test
import org.jetbrains.compose.resources.stringResource
import ru.example.docmanager.data.document.DocumentType
import ru.example.docmanager.ui.router.Route
import ru.example.docmanager.ui.screens.forms.powerofattorney.PowerOfAttorneyScreen

@Composable
fun FormScreen(
    destination: Route.Form,
    onStatusMessage: (String) -> Unit,
    navigateTo: (Route, Boolean) -> Unit,
    registerFabClickAction: ((() -> Unit)?) -> Unit,
    modifier: Modifier = Modifier,
) {
    val type = destination.type
    val document = destination.document
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier.fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        when (type) {
            DocumentType.POWER_OF_ATTORNEY -> PowerOfAttorneyScreen(
                document = document,
                onStatusMessage = onStatusMessage,
                navigateTo = navigateTo,
                registerFabClickAction = registerFabClickAction,
                scrollState = scrollState
            )

            DocumentType.TEST -> {
                Text(stringResource(Res.string.document_test))
            }
        }
    }
}