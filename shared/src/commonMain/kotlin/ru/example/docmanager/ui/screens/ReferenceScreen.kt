package ru.example.docmanager.ui.screens

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.window.core.layout.WindowSizeClass
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import kotlinx.datetime.format
import org.jetbrains.compose.resources.stringResource
import ru.example.docmanager.database.models.references.Reference
import ru.example.docmanager.database.repositories.references.ReferenceRepository
import ru.example.docmanager.ui.StringRegistry
import ru.example.docmanager.ui.Utils
import ru.example.docmanager.viewmodel.ReferenceViewModel

@Composable
fun ReferenceScreen(referenceViewModel: ReferenceViewModel) {
    val scope = rememberCoroutineScope()
    val reference = referenceViewModel.selectedReferenceRepository.collectAsState().value ?: return
    val referenceItems = referenceViewModel.referencesItems.collectAsState().value?.get(reference) ?: emptyList()
    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass

    var fieldValues by remember(referenceItems) {
        mutableStateOf(
            referenceItems.associate { item ->
                item.id to item.toMap()
            }
        )
    }

    Text(
        stringResource(StringRegistry.get(reference.name)),
        style = MaterialTheme.typography.titleLarge
    )
    Spacer(Modifier.height(16.dp))

    Column(
        Modifier.fillMaxSize().then(
            if (windowSizeClass.isWidthAtLeastBreakpoint(840)) Modifier else Modifier.horizontalScroll(rememberScrollState())
        )
    ) {
        referenceItems.forEach { item ->
            ReferenceInput(
                referenceRepository = reference,
                item = item,
                currentValues = fieldValues[item.id].orEmpty(),
                onFieldChange = { itemId, fieldName, newValue ->
                    val currentItemFields = fieldValues[itemId].orEmpty().toMutableMap()
                    currentItemFields[fieldName] = newValue
                    fieldValues = fieldValues + (itemId to currentItemFields)
                },
                referenceViewModel = referenceViewModel,
                scope = scope,
                windowSizeClass = windowSizeClass
            )
        }
    }
    Spacer(Modifier.height(16.dp))

    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Button(onClick = {
            scope.launch {
                fieldValues.forEach { (itemId, fields) ->
                    referenceViewModel.saveAll(reference, itemId, fields)
                }
            }
        }) { Text("Сохранить") }

        Button(onClick = {
            scope.launch { referenceViewModel.newItem(reference) }
        }) { Text("Новое поле") }
    }
}

@Composable
fun ReferenceInput(
    referenceRepository: ReferenceRepository<out Reference>,
    item: Reference,
    currentValues: Map<String, Any>,
    onFieldChange: (itemId: Int, fieldName: String, newValue: Any) -> Unit,
    referenceViewModel: ReferenceViewModel,
    scope: CoroutineScope,
    windowSizeClass: WindowSizeClass
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(item.id.toString())
        item.toMap().forEach { (name, value) ->
            InputField(
                label = stringResource(StringRegistry.get(name)),
                value = currentValues[name] ?: value,
                onValueChange = { newValue ->
                    onFieldChange(
                        item.id,
                        name,
                        newValue
                    )
                },
                windowSizeClass = windowSizeClass
            )
        }
        IconButton(
            onClick = {
                scope.launch {
                    referenceViewModel.removeItem(referenceRepository, item)
                }
            }
        ) {
            Icon(Icons.Filled.Remove, "Удалить")
        }
    }
}

@Composable
fun RowScope.InputField(
    label: String,
    value: Any,
    onValueChange: (Any) -> Unit,
    windowSizeClass: WindowSizeClass
) {
    val modifier = if (windowSizeClass.isWidthAtLeastBreakpoint(840))
        Modifier.weight(1f) else Modifier.widthIn(min = 300.dp)

    when (value) {
        is String -> {
            OutlinedTextField(
                label = { Text(label) },
                modifier = modifier,
                value = value,
                onValueChange = onValueChange
            )
        }
        is Int -> {
            var textValue by remember(value) {
                mutableStateOf(value.toString())
            }
            OutlinedTextField(
                label = { Text(label) },
                modifier = modifier,
                value = textValue,
                onValueChange = { newText ->
                    textValue = newText
                    newText.toIntOrNull()?.let { onValueChange(it) }
                }
            )
        }
        is LocalDate -> {
            var textValue by remember(value) {
                mutableStateOf(value.format(Utils.DATE_FORMAT))
            }
            OutlinedTextField(
                label = { Text(label) },
                modifier = modifier,
                value = textValue,
                onValueChange = { newText ->
                    textValue = newText
                    runCatching {
                        LocalDate.parse(newText, Utils.DATE_FORMAT)
                    }.getOrNull()?.let(onValueChange)
                }
            )
        }
        else -> Text("Unsupported type")
    }
}