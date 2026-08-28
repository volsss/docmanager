package tech.ilug.documentmanager.di

import android.content.ContentValues
import android.content.Context
import android.os.Build
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.widget.Toast
import org.apache.poi.xwpf.usermodel.XWPFDocument
import org.apache.poi.xwpf.usermodel.XWPFParagraph
import org.koin.core.context.GlobalContext
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream

class AndroidDocumentProcessor : DocumentProcessor {

    private fun getContext(): Context? {
        return try {
            GlobalContext.getOrNull()?.get<Context>()
        } catch (e: Throwable) {
            null
        }
    }

    private fun showToast(message: String) {
        val context = getContext() ?: return
        Handler(Looper.getMainLooper()).post {
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
        }
    }

    override fun processSave(
        documentName: String,
        documentResourceFile: String,
        headReplacements: Map<String, String>,
        bodyParts: Map<String, List<String>>
    ) {
        val fileName = if (documentName.endsWith(".docx", ignoreCase = true)) documentName else "$documentName.docx"
        val context = getContext()

        val generatedBytes = generateDocumentBytes(documentResourceFile, headReplacements, bodyParts)
        if (generatedBytes == null || generatedBytes.isEmpty()) {
            println("Failed to generate document: empty bytes or template not found")
            showToast("Ошибка при создании документа: шаблон не найден")
            return
        }

        if (context != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                put(
                    MediaStore.MediaColumns.MIME_TYPE,
                    "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
                )
                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOCUMENTS)
                put(MediaStore.MediaColumns.IS_PENDING, 1)
            }

            val collection = MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
            val uri = context.contentResolver.insert(collection, contentValues)

            if (uri != null) {
                try {
                    context.contentResolver.openOutputStream(uri, "w")?.use { output ->
                        output.write(generatedBytes)
                        output.flush()
                    }

                    contentValues.clear()
                    contentValues.put(MediaStore.MediaColumns.IS_PENDING, 0)
                    context.contentResolver.update(uri, contentValues, null, null)

                    showToast("Документ сохранен в папку 'Документы': $fileName")
                    return
                } catch (e: Exception) {
                    println("Error saving via MediaStore: ${e.message}")
                    try {
                        context.contentResolver.delete(uri, null, null)
                    } catch (ignored: Exception) {}
                }
            }
        }

        val documentsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
            ?: context?.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
            ?: context?.filesDir
            ?: File(".")

        if (!documentsDir.exists()) {
            documentsDir.mkdirs()
        }

        val destination = File(documentsDir, fileName)
        try {
            destination.parentFile?.mkdirs()
            FileOutputStream(destination).use { fos ->
                fos.write(generatedBytes)
                fos.flush()
            }
            showToast("Документ сохранен: ${destination.absolutePath}")
        } catch (e: Exception) {
            println("Error saving to file: ${e.message}")
            showToast("Ошибка сохранения: ${e.message}")
        }
    }

    override fun processPrint(
        documentName: String,
        documentResourceFile: String,
        headReplacements: Map<String, String>,
        bodyParts: Map<String, List<String>>
    ) {
        // Android Printing API можно сделать только для PDF и HTML, если есть желание -
        // можно предварительно конвертировать .docx в PDF или HTML и использовать PrintManager
        processSave(documentName, documentResourceFile, headReplacements, bodyParts)
    }

    private fun loadResource(context: Context?, documentResourceFile: String): InputStream? {
        val cleanName = documentResourceFile.removePrefix("/")

        if (context != null) {
            try {
                return context.assets.open(cleanName)
            } catch (ignored: Exception) {}
            try {
                return context.assets.open(documentResourceFile)
            } catch (ignored: Exception) {}
        }

        val loaders = listOfNotNull(
            AndroidDocumentProcessor::class.java.classLoader,
            Thread.currentThread().contextClassLoader,
            ClassLoader.getSystemClassLoader(),
            context?.classLoader
        )

        for (loader in loaders) {
            loader.getResourceAsStream(cleanName)?.let { return it }
            loader.getResourceAsStream("/$cleanName")?.let { return it }
            loader.getResourceAsStream("resources/$cleanName")?.let { return it }
            loader.getResourceAsStream("assets/$cleanName")?.let { return it }
        }

        AndroidDocumentProcessor::class.java.getResourceAsStream("/$cleanName")?.let { return it }
        AndroidDocumentProcessor::class.java.getResourceAsStream(cleanName)?.let { return it }

        return null
    }

    fun generateDocumentBytes(
        documentResourceFile: String,
        headReplacements: Map<String, String>,
        bodyParts: Map<String, List<String>>
    ): ByteArray? {
        val context = getContext()
        val inStream = loadResource(context, documentResourceFile) ?: return null
        return inStream.use { input ->
            ByteArrayOutputStream().use { baos ->
                processDocument(input, baos, headReplacements, bodyParts)
                baos.toByteArray()
            }
        }
    }

    fun processDocument(
        input: InputStream,
        output: OutputStream,
        headReplacements: Map<String, String>,
        bodyParts: Map<String, List<String>>
    ) {
        XWPFDocument(input).use { document ->
            document.paragraphs.forEach { paragraph ->
                processParagraph(paragraph, headReplacements)
            }

            document.tables.forEach { table ->
                table.rows.forEach { row ->
                    row.tableCells.forEach { cell ->
                        cell.paragraphs.forEach { paragraph ->
                            processParagraph(paragraph, headReplacements)
                        }
                    }
                }
            }

            processBodyParts(document, bodyParts)

            document.write(output)
        }
    }

    private fun processParagraph(paragraph: XWPFParagraph, replacements: Map<String, String>) {
        val text = paragraph.paragraphText ?: return

        var updatedText = text
        var matched = false

        replacements.forEach { (key, value) ->
            val placeholder = if (key.startsWith("{{") && key.endsWith("}}")) key else "{{$key}}"
            if (updatedText.contains(placeholder)) {
                updatedText = updatedText.replace(placeholder, value)
                matched = true
            } else if (updatedText.contains(key)) {
                updatedText = updatedText.replace(key, value)
                matched = true
            }
        }

        if (matched) {
            val runs = paragraph.runs
            if (runs.isNotEmpty()) {
                val firstRun = runs[0]
                val font = firstRun.fontFamily
                val size = firstRun.fontSizeAsDouble
                val bold = firstRun.isBold
                val italic = firstRun.isItalic

                for (i in runs.size - 1 downTo 1) {
                    paragraph.removeRun(i)
                }
                firstRun.setText(updatedText, 0)
                if (font != null) firstRun.fontFamily = font
                if (size != null && size > 0) firstRun.fontSize = size.toInt()
                firstRun.isBold = bold
                firstRun.isItalic = italic
            } else {
                val run = paragraph.createRun()
                run.setText(updatedText)
            }
        }
    }

    private fun processBodyParts(document: XWPFDocument, bodyParts: Map<String, List<String>>) {
        if (bodyParts.isEmpty()) return
        val maxRows = bodyParts.values.maxOfOrNull { it.size } ?: 0
        if (maxRows == 0) return

        val table = document.tables.firstOrNull { t ->
            if (t.rows.isEmpty()) return@firstOrNull false
            val headerRowText = t.rows[0].tableCells.joinToString(" ") { it.text }
            bodyParts.keys.any { key -> headerRowText.contains(key, ignoreCase = true) }
        } ?: (if (document.tables.size > 1) document.tables[1] else null) ?: return

        val headerCells = table.rows[0].tableCells
        val columnData = mutableMapOf<Int, List<String>>()

        for (colIdx in headerCells.indices) {
            val cellText = headerCells[colIdx].text.replace("\r", " ").replace("\n", " ").trim()
            val matchedEntry = bodyParts.entries.firstOrNull { (key, _) ->
                val cleanKey = key.trim()
                cellText.contains(cleanKey, ignoreCase = true) ||
                        cleanKey.contains(cellText, ignoreCase = true) ||
                        cellText.replace(" ", "").equals(cleanKey.replace(" ", ""), ignoreCase = true)
            }
            if (matchedEntry != null) {
                columnData[colIdx] = matchedEntry.value
            } else if (colIdx < bodyParts.size) {
                columnData[colIdx] = bodyParts.values.elementAt(colIdx)
            }
        }

        val startRowIdx = if (table.rows.size > 2) 2 else table.rows.size

        for (rowIndex in 0 until maxRows) {
            val row = if (rowIndex == 0 && table.rows.size > startRowIdx) {
                table.getRow(startRowIdx)
            } else {
                table.createRow()
            }

            for (colIdx in headerCells.indices) {
                val value = columnData[colIdx]?.getOrNull(rowIndex) ?: ""
                val cell = if (colIdx < row.tableCells.size) row.tableCells[colIdx] else row.addNewTableCell()
                val paragraph = if (cell.paragraphs.isNotEmpty()) cell.paragraphs[0] else cell.addParagraph()
                val runs = paragraph.runs
                if (runs.isNotEmpty()) {
                    for (r in runs.size - 1 downTo 1) {
                        paragraph.removeRun(r)
                    }
                    runs[0].setText(value, 0)
                } else {
                    val run = paragraph.createRun()
                    run.fontFamily = "Arial"
                    run.fontSize = 8
                    run.setText(value)
                }
            }
        }
    }
}
