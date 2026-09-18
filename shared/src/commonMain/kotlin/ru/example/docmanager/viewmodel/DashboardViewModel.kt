package ru.example.docmanager.viewmodel

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.FindInPage
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class DashboardViewModel {
    val selectedTab: StateFlow<Tab>
        field = MutableStateFlow(Tab.DOCUMENTS)

    fun selectTab(tab: Tab) {
        selectedTab.value = tab
    }
}

enum class Tab (val text: String, val icon: ImageVector) {
    DOCUMENTS("Документы", Icons.Default.FindInPage),
    DOCUMENT("Документ", Icons.Default.FindInPage),
    REFERENCES("Справочники", Icons.Default.Book),
    REFERENCE("Справочник", Icons.Default.Book),
    SETTINGS("Настройки", Icons.Default.Settings)
}