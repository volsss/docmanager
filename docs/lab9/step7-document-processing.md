# Шаг 7. Обработка документов

Экспорт документов в Word — ключевая функция docmanager. Мы используем **Apache POI** — библиотеку для работы с Office-файлами. Алгоритм:

1. Загружается шаблон `.docx` из ресурсов.
2. Плейсхолдеры в шапке (`{{number}}`, `{{organizationName}}` и т.д.) заменяются на реальные значения.
3. Таблица в теле заполняется данными из `bodyItems`.
4. Результат сохраняется в файл или отправляется на печать.

---

## Шаг 7.1. Интерфейс DocumentProcessor

**Файл:** `shared/src/commonMain/kotlin/.../domain/document/DocumentProcessor.kt`

```kotlin
package ru.example.docmanager.domain.document

interface DocumentProcessor {
    suspend fun processSave(
        documentName: String,
        documentResourceFile: String,
        document: Document,
        onFailure: (String) -> Unit,
        onSuccess: (String) -> Unit
    )

    suspend fun processPrint(
        documentName: String,
        documentResourceFile: String,
        document: Document,
        onFailure: (String) -> Unit,
        onSuccess: (String) -> Unit
    )
}
```

### Почему интерфейс?

Потому что реализация различается на Desktop и Android:
- **Desktop:** Java AWT/Swing для выбора файла, `Desktop.print()` для печати.
- **Android:** ContentResolver и Intent для сохранения и печати.

---

## Шаг 7.2. DocumentEngine — ядро обработки

**Файл:** `shared/src/commonMain/kotlin/.../domain/document/DocumentEngine.kt`

```kotlin
package ru.example.docmanager.domain.document

import org.apache.poi.xwpf.usermodel.XWPFDocument
import org.apache.poi.xwpf.usermodel.XWPFParagraph
import org.jetbrains.compose.resources.getString
import java.io.InputStream
import java.io.OutputStream
import java.util.concurrent.ConcurrentHashMap

object DocumentEngine {

    private val templateCache = ConcurrentHashMap<String, ByteArray>()
    private val placeholderRegex = Regex("""\{\{([^{}]+)\}\}""")

    // Кэширование шаблонов: загружаем .docx один раз
    fun getTemplateBytes(resourcePath: String, loader: (String) -> InputStream?): ByteArray? {
        val cleanPath = resourcePath.removePrefix("/")
        val cached = templateCache[cleanPath]
        if (cached != null) return cached

        val stream = loader(cleanPath) ?: loader("/$cleanPath") ?: loader(resourcePath) ?: return null
        val bytes = stream.use { it.readBytes() }
        if (bytes.isNotEmpty()) {
            templateCache[cleanPath] = bytes
        }
        return bytes
    }

    // Основная функция обработки
    suspend fun processDocument(
        input: InputStream,
        output: OutputStream,
        document: Document
    ) {
        val headReplacements = document.header.toMap().mapKeys { it.key.placeholder }
        val bodyParts = if (document is WithBody<*>) {
            document.body.toMapByColumns().mapKeys { getString(it.key.stringResource) }
        } else {
            null
        }
        processDocument(input, output, headReplacements, bodyParts)
    }

    fun processDocument(
        input: InputStream,
        output: OutputStream,
        headReplacements: Map<String, String>,
        bodyParts: Map<String, List<String>>?
    ) {
        XWPFDocument(input).use { doc ->
            // 1. Заменяем плейсхолдеры в шапке
            if (headReplacements.isNotEmpty()) {
                val preparedReplacements = prepareReplacements(headReplacements)
                doc.paragraphs.forEach { it.replacePlaceholders(preparedReplacements) }
                doc.tables.forEach { table ->
                    table.rows.forEach { row ->
                        row.tableCells.forEach { cell ->
                            cell.paragraphs.forEach { it.replacePlaceholders(preparedReplacements) }
                        }
                    }
                }
            }
            // 2. Заполняем таблицу тела
            if (!bodyParts.isNullOrEmpty()) {
                processBodyParts(doc, bodyParts)
            }
            doc.write(output)
        }
    }

    // Подготовка замен: поддержка {{key}} и {key}
    private class PreparedReplacements(
        val bracketMap: Map<String, String>,
        val exactMap: Map<String, String>,
        val plainKeys: List<Pair<String, String>>
    )

    private fun prepareReplacements(replacements: Map<String, String>): PreparedReplacements {
        val bracketMap = HashMap<String, String>(replacements.size * 2)
        val exactMap = HashMap<String, String>(replacements.size * 2)
        val plainKeys = mutableListOf<Pair<String, String>>()

        for ((key, value) in replacements) {
            exactMap[key] = value
            if (key.startsWith("{{") && key.endsWith("}}")) {
                val inner = key.substring(2, key.length - 2)
                bracketMap[inner] = value
                exactMap[inner] = value
            } else {
                bracketMap[key] = value
                val wrapped = "{{$key}}"
                exactMap[wrapped] = value
                plainKeys.add(key to value)
            }
        }
        return PreparedReplacements(bracketMap, exactMap, plainKeys)
    }

    // Замена плейсхолдеров в параграфе
    private fun XWPFParagraph.replacePlaceholders(prepared: PreparedReplacements) {
        val originalText = paragraphText ?: return
        if (originalText.isEmpty()) return

        val containsBrackets = originalText.contains("{{")
        val hasPlainKey = if (!containsBrackets && prepared.plainKeys.isNotEmpty()) {
            prepared.plainKeys.any { (key, _) -> originalText.contains(key) }
        } else false

        if (!containsBrackets && !hasPlainKey) return

        var updatedText = originalText
        var matched = false

        if (containsBrackets) {
            updatedText = placeholderRegex.replace(updatedText) { matchResult ->
                val tag = matchResult.groupValues[1]
                val replacement = prepared.bracketMap[tag]
                if (replacement != null) { matched = true; replacement } else matchResult.value
            }
        }

        if (prepared.plainKeys.isNotEmpty()) {
            for ((key, value) in prepared.plainKeys) {
                if (key in updatedText) { updatedText = updatedText.replace(key, value); matched = true }
            }
        }

        if (matched) {
            if (runs.isNotEmpty()) {
                val firstRun = runs[0]
                val font = firstRun.fontFamily
                val size = firstRun.fontSizeAsDouble
                val bold = firstRun.isBold
                val italic = firstRun.isItalic
                val color = firstRun.color
                val underline = firstRun.underline

                for (i in runs.lastIndex downTo 1) removeRun(i)
                firstRun.setText(updatedText, 0)
                if (font != null) firstRun.fontFamily = font
                if (size != null && size > 0) firstRun.fontSize = size.toInt()
                firstRun.isBold = bold
                firstRun.isItalic = italic
                if (color != null) firstRun.color = color
                if (underline != null) firstRun.underline = underline
            } else {
                createRun().setText(updatedText)
            }
        }
    }

    // Заполнение таблицы тела
    private fun processBodyParts(document: XWPFDocument, bodyParts: Map<String, List<String>>) {
        if (bodyParts.isEmpty()) return
        val maxRows = bodyParts.values.maxOfOrNull { it.size } ?: 0
        if (maxRows == 0) return

        val normalizedBodyParts = bodyParts.entries.map { entry ->
            val cleanKey = entry.key.trim()
            val noSpacesKey = cleanKey.replace(" ", "")
            Triple(cleanKey, noSpacesKey, entry.value)
        }

        // Ищем таблицу по заголовкам
        val table = document.tables.firstOrNull { t ->
            if (t.rows.isEmpty()) return@firstOrNull false
            val headerRowText = t.rows[0].tableCells.joinToString(" ") { it.text }
            normalizedBodyParts.any { (cleanKey, _, _) ->
                headerRowText.contains(cleanKey, ignoreCase = true)
            }
        } ?: document.tables.getOrNull(1) ?: return

        val headerCells = table.rows[0].tableCells
        val columnData = Array(headerCells.size) { colIdx ->
            val cellRawText = headerCells[colIdx].text
            val cellText = cellRawText.replace("\r", " ").replace("\n", " ").trim()
            val cellTextNoSpaces = cellText.replace(" ", "")

            val matchedValue = normalizedBodyParts.firstOrNull { (cleanKey, noSpacesKey, _) ->
                cellText.contains(cleanKey, ignoreCase = true) ||
                        cleanKey.contains(cellText, ignoreCase = true) ||
                        cellTextNoSpaces.equals(noSpacesKey, ignoreCase = true)
            }?.third

            matchedValue ?: bodyParts.values.elementAtOrNull(colIdx) ?: emptyList()
        }

        for (rowIndex in 0 until maxRows) {
            val row = if (rowIndex == 0 && table.rows.size > 2) table.getRow(2) else table.createRow()

            for (colIdx in headerCells.indices) {
                val value = columnData[colIdx].getOrNull(rowIndex) ?: ""
                val cell = if (colIdx < row.tableCells.size) row.tableCells[colIdx] else row.addNewTableCell()
                val paragraph = if (cell.paragraphs.isNotEmpty()) cell.paragraphs[0] else cell.addParagraph()
                if (paragraph.runs.isNotEmpty()) {
                    val firstRun = paragraph.runs[0]
                    for (r in paragraph.runs.lastIndex downTo 1) paragraph.removeRun(r)
                    firstRun.setText(value, 0)
                } else {
                    paragraph.createRun().apply {
                        fontFamily = "Arial"
                        fontSize = 8
                        setText(value)
                    }
                }
            }
        }
    }
}
```

### Как работает алгоритм?

1. **Кэширование шаблонов.** `templateCache` хранит байты загруженных `.docx`-файлов. Шаблон загружается один раз, а не при каждом экспорте.
2. **Замена плейсхолдеров.** Regex `\{\{([^{}]+)\}\}` находит все `{{key}}` в тексте и заменяет их на значения из `header.toMap()`.
3. **Сохранение форматирования.** При замене текста сохраняются шрифт, размер, жирность, курсив, цвет и подчёркивание первого run.
4. **Заполнение таблицы.** Находит таблицу по заголовкам колонок и заполняет данными из `body.toMapByColumns()`.

---

## Шаг 7.3. Платформенная реализация для Desktop

**Файл:** `shared/src/jvmMain/kotlin/.../data/document/JvmDocumentProcessor.kt`

```kotlin
package ru.example.docmanager.data.document

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.example.docmanager.domain.document.Document
import ru.example.docmanager.domain.document.DocumentEngine
import ru.example.docmanager.domain.document.DocumentProcessor
import java.awt.Desktop
import java.awt.FileDialog
import java.awt.Frame
import java.awt.GraphicsEnvironment
import java.io.*
import javax.print.PrintServiceLookup
import javax.swing.JFileChooser
import javax.swing.filechooser.FileNameExtensionFilter

class JvmDocumentProcessor : DocumentProcessor {

    override suspend fun processSave(
        documentName: String,
        documentResourceFile: String,
        document: Document,
        onFailure: (String) -> Unit,
        onSuccess: (String) -> Unit
    ) {
        val destination = chooseSaveFile("$documentName.docx") ?: return
        processSaveToFile(destination, documentResourceFile, document, onFailure, onSuccess)
    }

    override suspend fun processPrint(
        documentName: String,
        documentResourceFile: String,
        document: Document,
        onFailure: (String) -> Unit,
        onSuccess: (String) -> Unit
    ) {
        if (isPrintingAvailable()) {
            try {
                val tempFile = withContext(Dispatchers.IO) {
                    File.createTempFile("${documentName}_", ".docx").apply { deleteOnExit() }
                }
                processSaveToFile(tempFile, documentResourceFile, document, onFailure, onSuccess)
                withContext(Dispatchers.IO) {
                    if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.PRINT)) {
                        Desktop.getDesktop().print(tempFile)
                        onSuccess("Документ успешно распечатан")
                    } else {
                        onFailure("Печать не поддерживается устройством.")
                    }
                }
                return
            } catch (e: Exception) {
                onFailure("Ошибка печати: ${e.message}, попытка сохранения в файл")
            }
        }
        processSave(documentName, documentResourceFile, document, onFailure, onSuccess)
    }

    fun isPrintingAvailable(): Boolean {
        if (GraphicsEnvironment.isHeadless() || !Desktop.isDesktopSupported()) return false
        if (!Desktop.getDesktop().isSupported(Desktop.Action.PRINT)) return false
        return runCatching {
            PrintServiceLookup.lookupDefaultPrintService() != null ||
                    PrintServiceLookup.lookupPrintServices(null, null).isNotEmpty()
        }.getOrDefault(false)
    }

    fun chooseSaveFile(defaultFileName: String): File? {
        if (GraphicsEnvironment.isHeadless()) return null
        try {
            val dialog = FileDialog(null as Frame?, "Сохранить документ", FileDialog.SAVE).apply {
                file = defaultFileName
                setFilenameFilter { _, name -> name.endsWith(".docx", ignoreCase = true) }
                isVisible = true
            }
            val dir = dialog.directory
            val file = dialog.file
            if (dir != null && file != null) {
                val selected = File(dir, file)
                return if (!selected.name.endsWith(".docx", ignoreCase = true)) File(dir, "$file.docx") else selected
            }
        } catch (_: Throwable) {
            try {
                val chooser = JFileChooser().apply {
                    dialogTitle = "Сохранить документ"
                    selectedFile = File(defaultFileName)
                    fileFilter = FileNameExtensionFilter("Документы Word (*.docx)", "docx")
                }
                if (chooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION && chooser.selectedFile != null) {
                    val file = chooser.selectedFile
                    return if (!file.name.endsWith(".docx", ignoreCase = true))
                        File(file.parentFile, "${file.name}.docx") else file
                }
            } catch (_: Throwable) { }
        }
        return null
    }

    suspend fun processSaveToFile(
        destination: File,
        documentResourceFile: String,
        document: Document,
        onFailure: (String) -> Unit,
        onSuccess: (String) -> Unit
    ) = withContext(Dispatchers.IO) {
        val parent = destination.parentFile
        if (parent != null && !parent.exists() && !parent.mkdirs()) {
            onFailure("Ошибка создания директорий для: ${parent.absolutePath}")
            return@withContext
        }

        val templateBytes = DocumentEngine.getTemplateBytes(documentResourceFile) { path ->
            object {}.javaClass.getResourceAsStream("/$path")
                ?: object {}.javaClass.getResourceAsStream(path)
                ?: Thread.currentThread().contextClassLoader?.getResourceAsStream("/$path")
                ?: Thread.currentThread().contextClassLoader?.getResourceAsStream(path)
        }

        if (templateBytes == null) {
            onFailure("Форма не найдена: $documentResourceFile")
            return@withContext
        }

        ByteArrayInputStream(templateBytes).use { input ->
            BufferedOutputStream(FileOutputStream(destination)).use { fos ->
                DocumentEngine.processDocument(input, fos, document)
                onSuccess("Документ успешно сохранен")
            }
        }
    }
}
```

### Ключевые моменты

1. **`chooseSaveFile()`** — диалог сохранения файла. Сначала пытается AWT `FileDialog`, если не удалось — Swing `JFileChooser`.
2. **`isPrintingAvailable()`** — проверка, доступна ли печать на текущем устройстве.
3. **`processSaveToFile()`** — загружает шаблон из ресурсов, обрабатывает через `DocumentEngine` и сохраняет в файл.
4. **`withContext(Dispatchers.IO)`** — все файловые операции выполняются в IO-диспатчере.
