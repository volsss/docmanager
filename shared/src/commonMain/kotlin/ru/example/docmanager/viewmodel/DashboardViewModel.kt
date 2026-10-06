package ru.example.docmanager.viewmodel

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class DashboardViewModel {
    val selectedTab: StateFlow<Tab>
        field = MutableStateFlow(Tab.DOCUMENTS)

    fun selectTab(tab: Tab) {
        selectedTab.value = tab
    }
}