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
            if (!bodyParts.isNullOrEmpty()) {
                processBodyParts(doc, bodyParts)
            }
            doc.write(output)
        }
    }

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

    private fun XWPFParagraph.replacePlaceholders(prepared: PreparedReplacements) {
        val originalText = paragraphText ?: return
        if (originalText.isEmpty()) return

        val containsBrackets = originalText.contains("{{")
        val hasPlainKey = if (!containsBrackets && prepared.plainKeys.isNotEmpty()) {
            prepared.plainKeys.any { (key, _) -> originalText.contains(key) }
        } else {
            false
        }

        if (!containsBrackets && !hasPlainKey) return

        var updatedText = originalText
        var matched = false

        if (containsBrackets) {
            updatedText = placeholderRegex.replace(updatedText) { matchResult ->
                val tag = matchResult.groupValues[1]
                val replacement = prepared.bracketMap[tag]
                if (replacement != null) {
                    matched = true
                    replacement
                } else {
                    matchResult.value
                }
            }
        }

        if (prepared.plainKeys.isNotEmpty()) {
            for ((key, value) in prepared.plainKeys) {
                if (key in updatedText) {
                    updatedText = updatedText.replace(key, value)
                    matched = true
                }
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

                for (i in runs.lastIndex downTo 1) {
                    removeRun(i)
                }
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

    private fun processBodyParts(document: XWPFDocument, bodyParts: Map<String, List<String>>) {
        if (bodyParts.isEmpty()) return
        val maxRows = bodyParts.values.maxOfOrNull { it.size } ?: 0
        if (maxRows == 0) return

        val normalizedBodyParts = bodyParts.entries.map { entry ->
            val cleanKey = entry.key.trim()
            val noSpacesKey = cleanKey.replace(" ", "")
            Triple(cleanKey, noSpacesKey, entry.value)
        }

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

        val startRowIdx = if (table.rows.size > 2) 2 else table.rows.size

        var templateFont: String? = null
        var templateSize: Double? = null
        if (table.rows.size > startRowIdx) {
            val sampleRow = table.getRow(startRowIdx)
            val sampleRun = sampleRow.tableCells.firstOrNull()?.paragraphs?.firstOrNull()?.runs?.firstOrNull()
            if (sampleRun != null) {
                templateFont = sampleRun.fontFamily
                templateSize = sampleRun.fontSizeAsDouble
            }
        }

        for (rowIndex in 0 until maxRows) {
            val row = if (rowIndex == 0 && table.rows.size > startRowIdx) {
                table.getRow(startRowIdx)
            } else {
                table.createRow()
            }

            for (colIdx in headerCells.indices) {
                val value = columnData[colIdx].getOrNull(rowIndex) ?: ""
                val cell = if (colIdx < row.tableCells.size) row.tableCells[colIdx] else row.addNewTableCell()
                val paragraph = if (cell.paragraphs.isNotEmpty()) cell.paragraphs[0] else cell.addParagraph()
                if (paragraph.runs.isNotEmpty()) {
                    val firstRun = paragraph.runs[0]
                    for (r in paragraph.runs.lastIndex downTo 1) {
                        paragraph.removeRun(r)
                    }
                    firstRun.setText(value, 0)
                } else {
                    paragraph.createRun().apply {
                        fontFamily = templateFont ?: "Arial"
                        if (templateSize != null && templateSize > 0) {
                            fontSize = templateSize.toInt()
                        } else {
                            fontSize = 8
                        }
                        setText(value)
                    }
                }
            }
        }
    }
}
