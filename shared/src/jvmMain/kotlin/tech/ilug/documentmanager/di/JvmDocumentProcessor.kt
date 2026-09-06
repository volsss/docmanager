package tech.ilug.documentmanager.di

import org.apache.poi.xwpf.usermodel.XWPFDocument
import org.apache.poi.xwpf.usermodel.XWPFParagraph
import java.awt.Desktop
import java.awt.FileDialog
import java.awt.Frame
import java.awt.GraphicsEnvironment
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream
import javax.print.PrintServiceLookup
import javax.swing.JFileChooser
import javax.swing.filechooser.FileNameExtensionFilter

class JvmDocumentProcessor : DocumentProcessor {

    override fun processSave(
        documentName: String,
        documentResourceFile: String,
        headReplacements: Map<String, String>,
        bodyParts: Map<String, List<String>>
    ) {
        val destination = chooseSaveFile("$documentName.docx") ?: return
        processSaveToFile(destination, documentResourceFile, headReplacements, bodyParts)
    }

    override fun processPrint(
        documentName: String,
        documentResourceFile: String,
        headReplacements: Map<String, String>,
        bodyParts: Map<String, List<String>>
    ) {
        if (isPrintingAvailable()) {
            try {
                val tempFile = File.createTempFile("${documentName}_", ".docx").apply { deleteOnExit() }
                processSaveToFile(tempFile, documentResourceFile, headReplacements, bodyParts)
                Desktop.getDesktop().print(tempFile)
                return
            } catch (e: Exception) {
                println("Printing failed: ${e.message}, falling back to saving to file")
            }
        }
        processSave(documentName, documentResourceFile, headReplacements, bodyParts)
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
        } catch (e: Throwable) {
            try {
                val chooser = JFileChooser().apply {
                    dialogTitle = "Сохранить документ"
                    selectedFile = File(defaultFileName)
                    fileFilter = FileNameExtensionFilter("Документы Word (*.docx)", "docx")
                }
                if (chooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION && chooser.selectedFile != null) {
                    val file = chooser.selectedFile
                    return if (!file.name.endsWith(".docx", ignoreCase = true)) File(file.parentFile, "${file.name}.docx") else file
                }
            } catch (ignored: Throwable) {}
        }
        return null
    }

    fun processSaveToFile(
        destination: File,
        documentResourceFile: String,
        headReplacements: Map<String, String>,
        bodyParts: Map<String, List<String>>
    ) {
        destination.parentFile?.mkdirs()
        val blank = object {}.javaClass.getResourceAsStream("/$documentResourceFile") ?: run {
            println("Form not found: $documentResourceFile")
            return
        }
        blank.use { input ->
            FileOutputStream(destination).use { fos ->
                processDocument(input, fos, headReplacements, bodyParts)
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