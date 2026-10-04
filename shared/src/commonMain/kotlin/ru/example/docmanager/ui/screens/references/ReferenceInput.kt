package ru.example.docmanager.ui.screens.references

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.window.core.layout.WindowSizeClass
import org.jetbrains.compose.resources.stringResource
import ru.example.docmanager.database.models.references.Reference
import ru.example.docmanager.ui.StringRegistry
import ru.example.docmanager.ui.Utils

@Composable
fun ReferenceInput(
    item: Reference,
    currentValues: Map<String, Any>,
    onFieldChange: (itemId: Int, fieldName: String, newValue: Any) -> Unit,
    onRemove: () -> Unit,
    windowSizeClass: WindowSizeClass
) {
    val isWide = windowSizeClass.isWidthAtLeastBreakpoint(Utils.WIDE_BREAKPOINT)

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
                modifier = if (isWide) Modifier.weight(1f) else Modifier.widthIn(min = 300.dp)
            )
        }

        IconButton(onClick = onRemove) {
            Icon(Icons.Filled.Remove, "Удалить")
        }
    }
}
