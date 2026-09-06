package tech.ilug.documentmanager.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringResource
import tech.ilug.documentmanager.ui.StringRegistry
import tech.ilug.documentmanager.viewmodel.DashboardViewModel
import tech.ilug.documentmanager.viewmodel.ReferenceViewModel
import tech.ilug.documentmanager.viewmodel.Tab

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
        referenceViewModel.references.forEach { reference ->
            DocumentRow(
                title = stringResource(StringRegistry.get(reference.name)),
                onClick = {
                    referenceViewModel.selectReference(reference)
                    dashboardViewModel.selectTab(Tab.REFERENCE)
                }
            )
        }
    }
}