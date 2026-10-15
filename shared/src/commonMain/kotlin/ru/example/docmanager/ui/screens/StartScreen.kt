/*
 * Copyright (c) 2026 Andrew Z.
 * docmanager — a scalable document management system.
 */

package ru.example.docmanager.ui.screens

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import docmanager.shared.generated.resources.Res
import docmanager.shared.generated.resources.screen_document_types
import docmanager.shared.generated.resources.screen_documents
import docmanager.shared.generated.resources.screen_references
import docmanager.shared.generated.resources.screen_settings
import org.jetbrains.compose.resources.stringResource
import ru.example.docmanager.ui.navigation.Screen

@Composable
fun StartScreen(
    onDisconnect: () -> Unit,
    navigateTo: (Screen) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        StartScreenItem(
            screen = Screen.DocumentTypes,
            text = stringResource(Res.string.screen_document_types),
            navigateTo = navigateTo
        )
        StartScreenItem(
            screen = Screen.References,
            text = stringResource(Res.string.screen_references),
            navigateTo = navigateTo
        )
        StartScreenItem(
            screen = Screen.Settings,
            text = stringResource(Res.string.screen_settings),
            navigateTo = navigateTo
        )
    }
}

@Composable
fun ColumnScope.StartScreenItem(
    screen: Screen,
    text: String,
    navigateTo: (Screen) -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val cornerRadius by animateDpAsState(
        targetValue = if (isPressed) 128.dp else 32.dp
    )

    Box(
        modifier = Modifier.fillMaxWidth()
            .weight(1f)
    ) {
        Button(
            modifier = Modifier.fillMaxSize(),
            onClick = { navigateTo(screen) },
            shape = RoundedCornerShape(cornerRadius),
            interactionSource = interactionSource,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainer,
                contentColor = MaterialTheme.colorScheme.onSurface
            )
        ) {
            Icon(
                imageVector = screen.icon,
                contentDescription = text,
                modifier = Modifier.size(ButtonDefaults.ExtraLargeIconSize)
            )
            Spacer(Modifier.width(ButtonDefaults.ExtraLargeIconSpacing))
            Text(
                text = text,
                style = ButtonDefaults.textStyleFor(
                    ButtonDefaults.ExtraLargeContainerHeight
                ),
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Preview
@Composable
fun StartScreenPreview() {
    StartScreen(
        onDisconnect = { },
        navigateTo = { }
    )
}