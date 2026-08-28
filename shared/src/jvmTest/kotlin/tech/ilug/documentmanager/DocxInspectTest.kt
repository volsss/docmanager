package tech.ilug.documentmanager

import org.apache.poi.xwpf.usermodel.XWPFDocument
import tech.ilug.documentmanager.export.JvmDocumentProcessor
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class DocxInspectTest {
    @Test
    fun inspectDocx() {
        val stream = javaClass.getResourceAsStream("/Доверенность.docx")
        assertNotNull(stream)
        val sb = StringBuilder()
        XWPFDocument(stream).use { doc ->
            sb.appendLine("--- PARAGRAPHS ---")
            doc.paragraphs.forEachIndexed { i, p ->
                if (p.text.isNotBlank()) {
                    sb.appendLine("P$i: ${p.text}")
                }
            }
            sb.appendLine("--- TABLES ---")
            doc.tables.forEachIndexed { tIdx, table ->
                sb.appendLine("Table $tIdx (rows: ${table.rows.size}):")
                table.rows.forEachIndexed { rIdx, row ->
                    val cellsText = row.tableCells.map { cell ->
                        val runs = cell.paragraphs.flatMap { it.runs }.map { "${it.text()}[font=${it.fontFamily},sz=${it.fontSizeAsDouble}]" }
                        "Cell(text='${cell.text}', runs=$runs)"
                    }
                    sb.appendLine("  Row $rIdx: $cellsText")
                }
            }
        }
        File("build/docx_structure.txt").writeText(sb.toString())
    }

    @Test
    fun testProcessDocumentEndToEnd() {
        val stream = javaClass.getResourceAsStream("/Доверенность.docx")
        assertNotNull(stream)
        val processor = JvmDocumentProcessor()
        val out = ByteArrayOutputStream()
        val head = mapOf(
            "{{number}}" to "123",
            "{{dischargeDate}}" to "2026-08-28",
            "{{endDate}}" to "2026-09-28",
            "{{organizationName}}" to "ООО Рога и Копыта",
            "{{organizationConsumer}}" to "ООО Потребитель",
            "{{organizationPayer}}" to "ООО Плательщик",
            "{{organizationAccount}}" to "40702810000000000000",
            "{{individualJob}}" to "Экспедитор",
            "{{individualName}}" to "Иванов И.И.",
            "{{individualSeries}}" to "1234",
            "{{individualNumber}}" to "567890",
            "{{individualIssued}}" to "УВД г. Москвы",
            "{{individualDate}}" to "2020-01-15",
            "{{supplierName}}" to "ООО Поставщик",
            "{{supplierAgreement}}" to "Договор № 1"
        )
        val body = mapOf(
            "Номер по порядку" to listOf("1", "2"),
            "Материальные ценности" to listOf("Бумага A4", "Ручка шариковая"),
            "Единица измерения" to listOf("пачка", "шт"),
            "Количество (прописью)" to listOf("Две", "Десять")
        )
        processor.processDocument(stream, out, head, body)
        
        val resultDoc = XWPFDocument(ByteArrayInputStream(out.toByteArray()))
        val allText = resultDoc.paragraphs.joinToString("\n") { it.text } + "\n" +
                resultDoc.tables.joinToString("\n") { t -> t.rows.joinToString("\n") { r -> r.tableCells.joinToString(" | ") { it.text } } }
        
        println("[DEBUG_LOG] Processed result sample:")
        println(allText)
        
        assertTrue(allText.contains("123"))
        assertTrue(allText.contains("ООО Рога и Копыта"))
        assertTrue(allText.contains("Иванов И.И."))
        assertTrue(allText.contains("Бумага A4"))
        assertTrue(allText.contains("Ручка шариковая"))
        assertTrue(allText.contains("пачка"))
        assertTrue(allText.contains("Десять"))
    }

    @Test
    fun testProcessSaveToFile() {
        val tempFile = File.createTempFile("test_poa_", ".docx")
        try {
            val processor = JvmDocumentProcessor()
            val head = mapOf(
                "{{number}}" to "999",
                "{{dischargeDate}}" to "2026-08-28",
                "{{endDate}}" to "2026-09-28",
                "{{organizationName}}" to "Тестовая Организация",
                "{{organizationConsumer}}" to "Тестовый Потребитель",
                "{{organizationPayer}}" to "Тестовый Плательщик",
                "{{organizationAccount}}" to "40702810000000000000",
                "{{individualJob}}" to "Менеджер",
                "{{individualName}}" to "Петров П.П.",
                "{{individualSeries}}" to "9876",
                "{{individualNumber}}" to "543210",
                "{{individualIssued}}" to "УВД г. СПб",
                "{{individualDate}}" to "2021-02-20",
                "{{supplierName}}" to "Тестовый Поставщик",
                "{{supplierAgreement}}" to "Договор поставки № 42"
            )
            val body = mapOf(
                "Номер по порядку" to listOf("1"),
                "Материальные ценности" to listOf("Ноутбук"),
                "Единица измерения" to listOf("шт"),
                "Количество (прописью)" to listOf("Один")
            )

            processor.processSaveToFile(tempFile, head, body)

            assertTrue(tempFile.exists())
            assertTrue(tempFile.length() > 0)

            val resultDoc = XWPFDocument(tempFile.inputStream())
            val allText = resultDoc.paragraphs.joinToString("\n") { it.text } + "\n" +
                    resultDoc.tables.joinToString("\n") { t -> t.rows.joinToString("\n") { r -> r.tableCells.joinToString(" | ") { it.text } } }

            assertTrue(allText.contains("999"))
            assertTrue(allText.contains("Тестовая Организация"))
            assertTrue(allText.contains("Петров П.П."))
            assertTrue(allText.contains("Ноутбук"))
            assertTrue(allText.contains("Один"))
        } finally {
            tempFile.delete()
        }
    }

    @Test
    fun testIsPrintingAvailableCheck() {
        val processor = JvmDocumentProcessor()
        // Must not throw an exception
        val available = processor.isPrintingAvailable()
        println("[DEBUG_LOG] isPrintingAvailable = $available")
    }
}
