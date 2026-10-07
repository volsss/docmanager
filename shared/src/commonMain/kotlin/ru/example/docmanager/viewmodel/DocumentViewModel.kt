package ru.example.docmanager.viewmodel

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import ru.example.docmanager.document.DocumentType

class DocumentViewModel {
    val selectedDocumentType: StateFlow<DocumentType>
        field = MutableStateFlow(DocumentType.POWER_OF_ATTORNEY)

    fun selectDocument(documentType: DocumentType) {
        selectedDocumentType.value = documentType
    }
}
