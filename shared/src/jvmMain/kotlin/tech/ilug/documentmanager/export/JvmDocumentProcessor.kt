package tech.ilug.documentmanager.export

import org.apache.poi.xwpf.usermodel.XWPFDocument
import org.apache.poi.xwpf.usermodel.XWPFParagraph
import tech.ilug.documentmanager.di.DocumentProcessor
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

class JvmDocumentProcessor: DocumentProcessor {
    override fun processSave(
        headReplacements: Map<String, String>,
        bodyParts: Map<String, List<String>>
    ) {
        val destination = chooseSaveFile("Доверенность.docx") ?: return
        processSaveToFile(destination, headReplacements, bodyParts)
    }

    override fun processPrint(
        headReplacements: Map<String, String>,
        bodyParts: Map<String, List<String>>
    ) {
        if (isPrintingAvailable()) {
            try {
                val tempFile = File.createTempFile("Доверенность_", ".docx")
                tempFile.deleteOnExit()
                processSaveToFile(tempFile, headReplacements, bodyParts)
                Desktop.getDesktop().print(tempFile)
                return
            } catch (e: Exception) {
                println("Printing failed: ${e.message}, falling back to saving to file")
            }
        }
        processSave(headReplacements, bodyParts)
    }

    fun isPrintingAvailable(): Boolean {
        if (GraphicsEnvironment.isHeadless()) return false
        if (!Desktop.isDesktopSupported()) return false
        if (!Desktop.getDesktop().isSupported(Desktop.Action.PRINT)) return false
        return try {
            PrintServiceLookup.lookupDefaultPrintService() != null ||
                    PrintServiceLookup.lookupPrintServices(null, null).isNotEmpty()
        } catch (e: Throwable) {
            false
        }
    }

    fun chooseSaveFile(defaultFileName: String = "Доверенность.docx"): File? {
        if (GraphicsEnvironment.isHeadless()) {
            println("Headless environment, skipping file dialog")
            return null
        }

        try {
            val dialog = FileDialog(null as Frame?, "Сохранить документ", FileDialog.SAVE)
            dialog.file = defaultFileName
            dialog.setFilenameFilter { _, name -> name.endsWith(".docx", ignoreCase = true) }
            dialog.isVisible = true

            val dir = dialog.directory
            val file = dialog.file
            if (dir != null && file != null) {
                val selected = File(dir, file)
                return if (!selected.name.endsWith(".docx", ignoreCase = true)) {
                    File(dir, "$file.docx")
                } else {
                    selected
                }
            }
        } catch (e: Throwable) {
            println("FileDialog error: ${e.message}, falling back to JFileChooser")
            try {
                val chooser = JFileChooser().apply {
                    dialogTitle = "Сохранить документ"
                    selectedFile = File(defaultFileName)
                    fileFilter = FileNameExtensionFilter("Документы Word (*.docx)", "docx")
                }
                val result = chooser.showSaveDialog(null)
                if (result == JFileChooser.APPROVE_OPTION && chooser.selectedFile != null) {
                    var file = chooser.selectedFile
                    if (!file.name.endsWith(".docx", ignoreCase = true)) {
                        file = File(file.parentFile, "${file.name}.docx")
                    }
                    return file
                }
            } catch (e2: Throwable) {
                println("JFileChooser error: ${e2.message}")
            }
        }
        return null
    }

    fun processSaveToFile(
        destination: File,
        headReplacements: Map<String, String>,
        bodyParts: Map<String, List<String>>
    ) {
        destination.parentFile?.mkdirs()
        val blank = object {}.javaClass.getResourceAsStream("/Доверенность.docx")

        if (blank == null) {
            println("Form not found")
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
            bodyParts.keys.any { key -> headerRowText.contains(key, ignoreCase = true) } ||
                    headerRowText.contains("Материальн", ignoreCase = true)
        } ?: if (document.tables.size > 1) document.tables[1] else null ?: return

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