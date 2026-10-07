package ru.example.docmanager.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class DashboardViewModel : ViewModel() {
    val destination: StateFlow<DashboardDestination>
        field = MutableStateFlow<DashboardDestination>(
            DashboardDestination.Documents
        )

    fun navigateTo(destination: DashboardDestination) {
        this.destination.value = destination
    }
}