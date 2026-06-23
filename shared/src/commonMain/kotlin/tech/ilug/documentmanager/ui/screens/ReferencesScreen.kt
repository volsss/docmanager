package tech.ilug.documentmanager.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringResource
import tech.ilug.documentmanager.ui.StringRegistry
import tech.ilug.documentmanager.viewmodel.DashboardViewModel
import tech.ilug.documentmanager.viewmodel.ReferenceViewModel
import tech.ilug.documentmanager.viewmodel.Tab

@Composable
fun ReferencesScreen (
    referenceViewModel: ReferenceViewModel,
    dashboardViewModel: DashboardViewModel,
) {
    Column (
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        referenceViewModel.references.forEach { reference ->
            ReferenceRow (
                title = stringResource(StringRegistry.get(reference.name)),
                onClick = {
                    referenceViewModel.selectReference(reference)
                    dashboardViewModel.selectTab(Tab.REFERENCE)
                }
            )
        }
    }
}

@Composable
fun ReferenceRow (
    title: String,
    onClick: () -> Unit
) {
    Row (
        modifier = Modifier
            .background(MaterialTheme.colorScheme.secondaryContainer)
            .fillMaxWidth()
            .clickable(onClick = onClick,role = Role.Button)
            .padding(vertical = 16.dp, horizontal = 16.dp)
    ) {
        Text (
            title,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSecondaryContainer
        )
    }
}