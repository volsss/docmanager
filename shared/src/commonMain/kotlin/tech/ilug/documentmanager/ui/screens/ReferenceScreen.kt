package tech.ilug.documentmanager.ui.screens

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
import kotlinx.datetime.format.char
import org.jetbrains.compose.resources.stringResource
import tech.ilug.documentmanager.model.Reference
import tech.ilug.documentmanager.ui.StringRegistry
import tech.ilug.documentmanager.viewmodel.ReferenceViewModel

val DATE_FORMAT = LocalDate.Format { day(); char('.'); monthNumber(); char('.'); year() }

@Composable
fun ReferenceScreen(referenceViewModel: ReferenceViewModel) {
    val scope = rememberCoroutineScope()
    val reference = referenceViewModel.selectedReference.collectAsState().value ?: return
    val referenceItems = referenceViewModel.referencesItems.collectAsState().value?.get(reference) ?: emptyList()
    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass

    var fieldValues by remember(referenceItems) {
        mutableStateOf(
            referenceItems.associate { item ->
                item.id to item.asFields().associate { it.name to it.value }
            }
        )
    }

    fun updateFieldValue(itemId: Int, fieldName: String, newValue: Any) {
        val currentItemFields = fieldValues[itemId].orEmpty().toMutableMap()
        currentItemFields[fieldName] = newValue
        fieldValues = fieldValues + (itemId to currentItemFields)
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
                reference = reference,
                item = item,
                currentValues = fieldValues[item.id].orEmpty(),
                onFieldChange = ::updateFieldValue,
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
    reference: Reference<out Reference.Item>,
    item: Reference.Item,
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
        item.asFields().forEach { field ->
            InputField(
                label = stringResource(StringRegistry.get(field.name)),
                value = currentValues[field.name] ?: field.value,
                onValueChange = { newValue -> onFieldChange(item.id, field.name, newValue) },
                windowSizeClass = windowSizeClass
            )
        }
        IconButton(onClick = { scope.launch { referenceViewModel.removeItem(reference, item) } }) {
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
    val modifier = if (windowSizeClass.isWidthAtLeastBreakpoint(840)) Modifier.weight(1f) else Modifier.widthIn(min = 300.dp)

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
            var textValue by remember(value) { mutableStateOf(value.toString()) }
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
            var textValue by remember(value) { mutableStateOf(value.format(DATE_FORMAT)) }
            OutlinedTextField(
                label = { Text(label) },
                modifier = modifier,
                value = textValue,
                onValueChange = { newText ->
                    textValue = newText
                    runCatching { LocalDate.parse(newText, DATE_FORMAT) }.getOrNull()?.let(onValueChange)
                }
            )
        }
        else -> Text("Unsupported type")
    }
}