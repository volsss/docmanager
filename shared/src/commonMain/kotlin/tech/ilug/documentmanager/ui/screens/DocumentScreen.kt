package tech.ilug.documentmanager.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ContainedLoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import tech.ilug.documentmanager.model.DocumentType
import tech.ilug.documentmanager.ui.screens.forms.powerOfAttorney.PowerOfAttorneyScreen
import tech.ilug.documentmanager.viewmodel.DocumentViewModel
import tech.ilug.documentmanager.viewmodel.ReferenceViewModel

@Composable
fun DocumentScreen (
    documentsViewModel: DocumentViewModel,
    referenceViewModel: ReferenceViewModel
) {
    val type = documentsViewModel.selectedDocumentType.collectAsState()
    if (type.value?.title == null) {
        Text("Документ", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(16.dp))
    }

    Column (
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        when (type.value) {
            DocumentType.POWER_OF_ATTORNEY -> PowerOfAttorneyScreen(referenceViewModel)
            else -> {
                Box (
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    ContainedLoadingIndicator(
                        modifier = Modifier.size(128.dp)
                    )
                }
            }
        }
    }
}