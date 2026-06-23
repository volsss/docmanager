package tech.ilug.documentmanager.viewmodel

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ConnectionViewModel {
    val error: StateFlow<String?>
        field = MutableStateFlow<String?>(null)

    fun setError(text: String?) {
        error.value = text
    }
}