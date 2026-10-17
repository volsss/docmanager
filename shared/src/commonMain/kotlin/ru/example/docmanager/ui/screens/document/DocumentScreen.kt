/*
 * Copyright (c) 2026 Andrew Z.
 * docmanager — a scalable document management system.
 */

package ru.example.docmanager.ui.screens.document

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
import ru.example.docmanager.ui.screens.document.forms.powerOfAttorney.PowerOfAttorneyScreen

@Composable
fun DocumentScreen(
    destination: Route.Document,
    modifier: Modifier = Modifier,
) {
    val type = destination.type

    Column(
        modifier = modifier.fillMaxSize().padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        when (type) {
            DocumentType.POWER_OF_ATTORNEY -> PowerOfAttorneyScreen()
            DocumentType.TEST -> {
                Text(stringResource(Res.string.document_test))
            }
        }
    }
}