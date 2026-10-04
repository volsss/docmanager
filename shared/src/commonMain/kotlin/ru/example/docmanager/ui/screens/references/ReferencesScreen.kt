package ru.example.docmanager.ui.screens.references

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringResource
import ru.example.docmanager.database.models.references.ReferenceType
import ru.example.docmanager.ui.screens.DocumentRow
import ru.example.docmanager.viewmodel.DashboardViewModel
import ru.example.docmanager.viewmodel.ReferenceViewModel
import ru.example.docmanager.viewmodel.Tab

@Composable
fun ReferencesScreen(
    referenceViewModel: ReferenceViewModel,
    dashboardViewModel: DashboardViewModel,
) {
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        ReferenceType.entries.forEach { reference ->
            DocumentRow(
                title = stringResource(reference.stringResource),
                onClick = {
                    referenceViewModel.selectReference(reference)
                    dashboardViewModel.selectTab(Tab.REFERENCE)
                }
            )
        }
    }
}