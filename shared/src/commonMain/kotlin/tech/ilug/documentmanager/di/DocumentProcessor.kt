package tech.ilug.documentmanager.di

interface DocumentProcessor {
    /**
     * Save the document to the file system
     * @param documentName Name of the document to save
     * @param documentResourceFile Name of the document resource file
     * @param headReplacements Map of placeholder -> value
     * @param bodyParts Map of columnName -> List of values
     */
    fun processSave(
        documentName: String,
        documentResourceFile: String,
        headReplacements: Map<String, String>,
        bodyParts: Map<String, List<String>>
    )

    /**
     * Print the document to the printer
     * @param documentName Name of the document to print
     * @param documentResourceFile Name of the document resource file
     * @param headReplacements Map of placeholder -> value
     * @param bodyParts Map of columnName -> List of values
     */
    fun processPrint(
        documentName: String,
        documentResourceFile: String,
        headReplacements: Map<String, String>,
        bodyParts: Map<String, List<String>>
    )
}