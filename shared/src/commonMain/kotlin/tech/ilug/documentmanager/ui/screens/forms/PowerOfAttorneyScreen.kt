package tech.ilug.documentmanager.ui.screens.forms

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import tech.ilug.documentmanager.di.getDocumentProcessor

@Composable
fun PowerOfAttorneyScreen() {
    var number by remember { mutableStateOf("0") }
    var dischargeDate by remember { mutableStateOf("") }
    var endDate by remember { mutableStateOf("") }
    var individualId by remember { mutableStateOf("0") }
    var organizationId by remember { mutableStateOf("0") }

    OutlinedTextField (
        value = number,
        onValueChange = { number = it },
        label = { Text("Номер") },
        singleLine = true,
        modifier = Modifier.widthIn(500.dp)
    )
    OutlinedTextField (
        value = dischargeDate,
        onValueChange = { dischargeDate = it },
        label = { Text("Дата выписки") },
        singleLine = true,
        modifier = Modifier.widthIn(500.dp)
    )
    OutlinedTextField (
        value = endDate,
        onValueChange = { endDate = it },
        label = { Text("Дата окончания") },
        singleLine = true,
        modifier = Modifier.widthIn(500.dp)
    )
    OutlinedTextField (
        value = individualId,
        onValueChange = { individualId = it },
        label = { Text("Физ. лицо") },
        singleLine = true,
        modifier = Modifier.widthIn(500.dp)
    )
    OutlinedTextField (
        value = organizationId,
        onValueChange = { organizationId = it },
        label = { Text("Организация") },
        singleLine = true,
        modifier = Modifier.widthIn(500.dp)
    )

    Row {
        Button({

        }) {
            Text("Сохранить")
        }
        Spacer(Modifier.width(8.dp))
        Button({
            getDocumentProcessor().process(mapOf(
                "{{number}}" to number,
                "{{dischargeDate}}" to dischargeDate,
                "{{endDate}}" to endDate,
            ))
        }) {
            Text("Печать")
        }
    }
}