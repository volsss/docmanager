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

    private fun getContext(): Context? = runCatching { GlobalContext.getOrNull()?.get<Context>() }.getOrNull()

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
                put(MediaStore.MediaColumns.MIME_TYPE, "application/vnd.openxmlformats-officedocument.wordprocessingml.document")
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
                    runCatching { context.contentResolver.delete(uri, null, null) }
                }
            }
        }

        val documentsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
            ?: context?.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
            ?: context?.filesDir
            ?: File(".")

        if (!documentsDir.exists()) documentsDir.mkdirs()

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
        processSave(documentName, documentResourceFile, headReplacements, bodyParts)
    }

    private fun loadResource(context: Context?, documentResourceFile: String): InputStream? {
        val cleanName = documentResourceFile.removePrefix("/")

        if (context != null) {
            runCatching { return context.assets.open(cleanName) }
            runCatching { return context.assets.open(documentResourceFile) }
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
        val inStream = loadResource(getContext(), documentResourceFile) ?: return null
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
        XWPFDocument(input).use { doc ->
            doc.paragraphs.forEach { it.replacePlaceholders(headReplacements) }
            doc.tables.forEach { table ->
                table.rows.forEach { row ->
                    row.tableCells.forEach { cell ->
                        cell.paragraphs.forEach { it.replacePlaceholders(headReplacements) }
                    }
                }
            }
            processBodyParts(doc, bodyParts)
            doc.write(output)
        }
    }

    private fun XWPFParagraph.replacePlaceholders(replacements: Map<String, String>) {
        val originalText = paragraphText ?: return
        var updatedText = originalText
        var matched = false

        for ((key, value) in replacements) {
            val placeholder = if (key.startsWith("{{") && key.endsWith("}}")) key else "{{$key}}"
            if (placeholder in updatedText) {
                updatedText = updatedText.replace(placeholder, value)
                matched = true
            } else if (key in updatedText) {
                updatedText = updatedText.replace(key, value)
                matched = true
            }
        }

        if (matched) {
            if (runs.isNotEmpty()) {
                val firstRun = runs[0]
                val font = firstRun.fontFamily
                val size = firstRun.fontSizeAsDouble
                val bold = firstRun.isBold
                val italic = firstRun.isItalic

                for (i in runs.lastIndex downTo 1) removeRun(i)
                firstRun.setText(updatedText, 0)
                if (font != null) firstRun.fontFamily = font
                if (size != null && size > 0) firstRun.fontSize = size.toInt()
                firstRun.isBold = bold
                firstRun.isItalic = italic
            } else {
                createRun().setText(updatedText)
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
            bodyParts.keys.any { headerRowText.contains(it, ignoreCase = true) }
        } ?: document.tables.getOrNull(1) ?: return

        val headerCells = table.rows[0].tableCells
        val columnData = headerCells.indices.associateWith { colIdx ->
            val cellText = headerCells[colIdx].text.replace("\r", " ").replace("\n", " ").trim()
            val matchedEntry = bodyParts.entries.firstOrNull { (key, _) ->
                val cleanKey = key.trim()
                cellText.contains(cleanKey, ignoreCase = true) ||
                    cleanKey.contains(cellText, ignoreCase = true) ||
                    cellText.replace(" ", "").equals(cleanKey.replace(" ", ""), ignoreCase = true)
            }
            matchedEntry?.value ?: bodyParts.values.elementAtOrNull(colIdx) ?: emptyList()
        }

        val startRowIdx = if (table.rows.size > 2) 2 else table.rows.size

        for (rowIndex in 0 until maxRows) {
            val row = if (rowIndex == 0 && table.rows.size > startRowIdx) table.getRow(startRowIdx) else table.createRow()
            for (colIdx in headerCells.indices) {
                val value = columnData[colIdx]?.getOrNull(rowIndex) ?: ""
                val cell = if (colIdx < row.tableCells.size) row.tableCells[colIdx] else row.addNewTableCell()
                val paragraph = if (cell.paragraphs.isNotEmpty()) cell.paragraphs[0] else cell.addParagraph()
                if (paragraph.runs.isNotEmpty()) {
                    for (r in paragraph.runs.lastIndex downTo 1) paragraph.removeRun(r)
                    paragraph.runs[0].setText(value, 0)
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
