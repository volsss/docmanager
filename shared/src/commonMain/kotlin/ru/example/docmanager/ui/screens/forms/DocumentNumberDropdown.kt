/*
 * Copyright (c) 2026 Andrew Z.
 * docmanager — a scalable document management system.
 */

package ru.example.docmanager.ui.screens.forms

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import ru.example.docmanager.document.Document
import ru.example.docmanager.ui.Utils

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
                                    "№ ${doc.header.number} от ${Utils.DATE_FORMAT.format(doc.header.dischargeDate)}",
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