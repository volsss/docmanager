package tech.ilug.documentmanager.viewmodel

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import tech.ilug.documentmanager.database.models.documents.DocumentType

class DocumentViewModel {
    val selectedDocumentType: StateFlow<DocumentType?>
        field = MutableStateFlow<DocumentType?>(null)

    fun selectDocument(documentType: DocumentType) {
        selectedDocumentType.value = documentType
    }
}
