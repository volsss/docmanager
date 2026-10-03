package ru.example.docmanager.ui.screens.forms

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import ru.example.docmanager.database.models.documents.Document
import ru.example.docmanager.ui.screens.DATE_FORMAT
import kotlin.collections.forEach

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T: Document> DocumentNumberDropdown(
    number: String,
    onNumberChange: (String) -> Unit,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    existingDocuments: List<T>,
    onNewDocumentClick: () -> Unit,
    onDocumentSelected: (T) -> Unit,
    modifier: Modifier = Modifier
) {
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = onExpandedChange,
        modifier = modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = number,
            onValueChange = onNumberChange,
            label = { Text("Номер документа (выберите из списка или введите)") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryEditable)
                .fillMaxWidth(),
            singleLine = true
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { onExpandedChange(false) }
        ) {
            DropdownMenuItem(
                text = { Text("Создать новый документ") },
                onClick = {
                    onNewDocumentClick()
                    onExpandedChange(false)
                }
            )
            if (existingDocuments.isEmpty()) {
                DropdownMenuItem(
                    text = { Text("Нет сохраненных документов в базе") },
                    onClick = {},
                    enabled = false
                )
            } else {
                existingDocuments.forEach { doc ->
                    DropdownMenuItem(
                        text = {
                            Column {
                                Text(
                                    "№ ${doc.header.number} от ${DATE_FORMAT.format(doc.header.dischargeDate)}",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        },
                        onClick = { onDocumentSelected(doc) }
                    )
                }
            }
        }
    }
}