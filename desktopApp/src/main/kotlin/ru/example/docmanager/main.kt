package ru.example.docmanager

import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import docmanager.desktopapp.generated.resources.Res
import docmanager.desktopapp.generated.resources.icon
import org.jetbrains.compose.resources.painterResource
import org.koin.core.context.GlobalContext.startKoin
import ru.example.docmanager.di.sharedModule
import ru.example.docmanager.ui.App

fun main() {
    startKoin {
        modules(sharedModule)
    }

    application {
        Window(
            onCloseRequest = ::exitApplication,
            state = rememberWindowState(
                position = WindowPosition.Aligned(Alignment.Center),
                width = 1000.dp,
                height = 800.dp
            ),
            title = "Документооборот",
            icon = painterResource(Res.drawable.icon)
        ) {
            App()
        }
    }
}
