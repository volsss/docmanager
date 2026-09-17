package tech.ilug.documentmanager.di

interface DocumentProcessor {
    fun processSave(
        documentName: String,
        documentResourceFile: String,
        headReplacements: Map<String, String>,
        bodyParts: Map<String, List<String>>
    )

    fun processPrint(
        documentName: String,
        documentResourceFile: String,
        headReplacements: Map<String, String>,
        bodyParts: Map<String, List<String>>
    )
}