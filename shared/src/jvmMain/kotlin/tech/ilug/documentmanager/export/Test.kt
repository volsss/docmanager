package tech.ilug.documentmanager.export

import org.apache.poi.xwpf.usermodel.XWPFDocument
import org.apache.poi.xwpf.usermodel.XWPFParagraph
import tech.ilug.documentmanager.di.DocumentProcessor
import java.io.File
import java.io.FileOutputStream

class Test: DocumentProcessor {
    override fun process(replacements: Map<String, String>) {
        val destination = File("C:\\Users\\godly\\Documents\\Доверенность1.docx")
        val blank = object {}.javaClass.getResourceAsStream("/Доверенность.docx")

        if (blank == null) {
            println("Form not found")
            return
        }

        blank.use {
            XWPFDocument(it).use { document ->
                document.paragraphs.forEach { paragraph ->
                    processParagraph(paragraph, replacements)
                }

                document.tables.forEach { table ->
                    table.rows.forEach { row ->
                        row.tableCells.forEach { cell ->
                            cell.paragraphs.forEach { paragraph ->
                                processParagraph(paragraph, replacements)
                            }
                        }
                    }
                }

                FileOutputStream(destination).use { fos ->
                    document.write(fos)
                }
            }
        }
    }

    private fun processParagraph(paragraph: XWPFParagraph, replacements: Map<String, String>) {
        val text = paragraph.paragraphText ?: return

        if (replacements.keys.any { text.contains(it) }) {
            var updatedText = text
            replacements.forEach { (key, value) ->
                updatedText = updatedText.replace(key, value)
            }

            val runs = paragraph.runs
            if (runs.isNotEmpty()) {
                for (i in runs.size - 1 downTo 1) {
                    paragraph.removeRun(i)
                }
                runs[0].setText(updatedText, 0)
            } else {
                paragraph.createRun().setText(updatedText)
            }
        }
    }
}