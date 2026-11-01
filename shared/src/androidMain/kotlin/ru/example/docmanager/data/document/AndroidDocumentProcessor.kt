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

import android.content.ContentValues
import android.content.Context
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.compose.resources.getString
import ru.example.docmanager.domain.document.Document
import ru.example.docmanager.domain.document.DocumentEngine
import ru.example.docmanager.domain.document.DocumentProcessor
import ru.example.docmanager.domain.document.WithBody
import java.io.*

class AndroidDocumentProcessor(
    private val context: Context
) : DocumentProcessor {

    override suspend fun processSave(
        documentName: String,
        documentResourceFile: String,
        document: Document,
        onFailure: (String) -> Unit,
        onSuccess: (String) -> Unit
    ) {
        val fileName =
            if (documentName.endsWith(".docx", ignoreCase = true))
                documentName
            else "$documentName.docx"
        val headReplacements = document.header.toMap().mapKeys { it.key.placeholder }
        val bodyParts =
            if (document is WithBody<*>) document.body.toMapByColumns().mapKeys {
                getString(it.key.stringResource)
            } else null

        val generatedBytes = generateDocumentBytes(documentResourceFile, headReplacements, bodyParts)
        if (generatedBytes == null || generatedBytes.isEmpty()) {
            onFailure("Ошибка при создании документа: шаблон не найден")
            return
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
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

                    onSuccess("Документ сохранен в папку 'Документы': $fileName")
                    return
                } catch (e: Exception) {
                    onFailure("Ошибка сохранения через MediaStore: ${e.message}")
                    runCatching { context.contentResolver.delete(uri, null, null) }
                }
            }
        }

        val documentsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
            ?: context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
            ?: context.filesDir
            ?: File(".")

        if (!documentsDir.exists()) documentsDir.mkdirs()

        val destination = File(documentsDir, fileName)
        try {
            destination.parentFile?.mkdirs()
            withContext(Dispatchers.IO) {
                BufferedOutputStream(FileOutputStream(destination)).use { fos ->
                    fos.write(generatedBytes)
                    fos.flush()
                }
                onSuccess("Документ сохранен: ${destination.absolutePath}")
            }
        } catch (e: Exception) {
            onFailure("Ошибка сохранения: ${e.message}")
        }
    }

    override suspend fun processPrint(
        documentName: String,
        documentResourceFile: String,
        document: Document,
        onFailure: (String) -> Unit,
        onSuccess: (String) -> Unit
    ) {
        processSave(documentName, documentResourceFile, document, onFailure, onSuccess)
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
        bodyParts: Map<String, List<String>>?
    ): ByteArray? {
        val templateBytes = DocumentEngine.getTemplateBytes(documentResourceFile) { path ->
            loadResource(context, path)
        } ?: return null

        return ByteArrayInputStream(templateBytes).use { input ->
            ByteArrayOutputStream(templateBytes.size + 4096).use { baos ->
                processDocument(input, baos, headReplacements, bodyParts)
                baos.toByteArray()
            }
        }
    }

    fun processDocument(
        input: InputStream,
        output: OutputStream,
        headReplacements: Map<String, String>,
        bodyParts: Map<String, List<String>>?
    ) {
        DocumentEngine.processDocument(input, output, headReplacements, bodyParts)
    }
}
