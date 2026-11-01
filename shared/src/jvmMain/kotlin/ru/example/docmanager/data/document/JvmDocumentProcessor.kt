/*
 * Copyright (c) 2026 Andrew Z.
 * docmanager — a scalable document management system.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License v3.0.
 *
 * Distributed WITHOUT ANY WARRANTY. See the GNU General Public License
 * for more details: <https://gnu.org>
 */

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
        document: Document
    ) {
        val destination = chooseSaveFile("$documentName.docx") ?: return
        processSaveToFile(destination, documentResourceFile, document)
    }

    override suspend fun processPrint(
        documentName: String,
        documentResourceFile: String,
        document: Document
    ) {
        if (isPrintingAvailable()) {
            try {
                val tempFile = withContext(Dispatchers.IO) {
                    File.createTempFile("${documentName}_", ".docx").apply {
                        deleteOnExit()
                    }
                }

                processSaveToFile(tempFile, documentResourceFile, document)

                withContext(Dispatchers.IO) {
                    if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.PRINT)) {
                        Desktop.getDesktop().print(tempFile)
                    } else {
                        throw UnsupportedOperationException("Print action is not supported on this platform/desktop.")
                    }
                }
                return
            } catch (e: Exception) {
                System.err.println("Printing failed: ${e.message}, falling back to saving to file")
            }
        }
        processSave(documentName, documentResourceFile, document)
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
                    return if (!file.name.endsWith(".docx", ignoreCase = true)) File(
                        file.parentFile,
                        "${file.name}.docx"
                    ) else file
                }
            } catch (_: Throwable) {
            }
        }
        return null
    }

    suspend fun processSaveToFile(
        destination: File,
        documentResourceFile: String,
        document: Document
    ) = withContext(Dispatchers.IO) {
        val parent = destination.parentFile
        if (parent != null && !parent.exists() && !parent.mkdirs()) {
            throw IOException("Failed to create directories for path: ${parent.absolutePath}")
        }

        val templateBytes = DocumentEngine.getTemplateBytes(documentResourceFile) { path ->
            object {}.javaClass.getResourceAsStream("/$path")
                ?: object {}.javaClass.getResourceAsStream(path)
                ?: Thread.currentThread().contextClassLoader?.getResourceAsStream("/$path")
                ?: Thread.currentThread().contextClassLoader?.getResourceAsStream(path)
        }

        if (templateBytes == null) {
            System.err.println("Form not found: $documentResourceFile")
            return@withContext
        }

        ByteArrayInputStream(templateBytes).use { input ->
            BufferedOutputStream(FileOutputStream(destination)).use { fos ->
                processDocument(input, fos, document)
            }
        }
    }

    suspend fun processDocument(
        input: InputStream,
        output: OutputStream,
        document: Document
    ) {
        DocumentEngine.processDocument(input, output, document)
    }
}