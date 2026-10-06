package ru.example.docmanager.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringResource
import ru.example.docmanager.documents.DocumentType
import ru.example.docmanager.ui.screens.forms.powerOfAttorney.PowerOfAttorneyScreen
import ru.example.docmanager.viewmodel.DocumentViewModel
import ru.example.docmanager.viewmodel.ReferenceViewModel

@Composable
fun DocumentScreen(
    documentsViewModel: DocumentViewModel,
    referenceViewModel: ReferenceViewModel
) {
    val type by documentsViewModel.selectedDocumentType.collectAsState()
    Text(stringResource(type.stringResource), style = MaterialTheme.typography.titleLarge)
    Spacer(Modifier.height(8.dp))

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        when (type) {
            DocumentType.POWER_OF_ATTORNEY -> PowerOfAttorneyScreen(referenceViewModel)
        }
    }
}