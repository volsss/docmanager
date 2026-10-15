/*
 * Copyright (c) 2026 Andrew Z.
 * docmanager — a scalable document management system.
 */

package ru.example.docmanager.ui.screens

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
import ru.example.docmanager.document.DocumentType
import ru.example.docmanager.ui.components.ScreenTopBar
import ru.example.docmanager.ui.navigation.Screen
import ru.example.docmanager.ui.screens.forms.powerOfAttorney.PowerOfAttorneyScreen

@Composable
fun DocumentScreen (
    destination: Screen.Document,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val type = destination.type

    Column(
        modifier = modifier.fillMaxSize()
    ) {
        ScreenTopBar(
            title = stringResource(destination.type.stringResource),
            onBack = onBack
        )
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp)
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
}