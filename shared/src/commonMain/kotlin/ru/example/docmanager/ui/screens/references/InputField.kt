package ru.example.docmanager.ui.screens.references

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import kotlinx.datetime.LocalDate
import kotlinx.datetime.format
import ru.example.docmanager.ui.Utils

@Composable
fun RowScope.InputField(
    label: String,
    value: Any,
    onValueChange: (Any) -> Unit,
    modifier: Modifier = Modifier
) {
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