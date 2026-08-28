package tech.ilug.documentmanager.di

actual fun getPlatform(): Platform = Platform.ANDROID
actual fun getDocumentProcessor(): DocumentProcessor = object : DocumentProcessor {
    override fun processSave(
        headReplacements: Map<String, String>,
        bodyParts: Map<String, List<String>>
    ) {
        println("DocumentProcessor.processSave is not implemented for Android yet")
    }

    override fun processPrint(
        headReplacements: Map<String, String>,
        bodyParts: Map<String, List<String>>
    ) {
        println("DocumentProcessor.processPrint is not implemented for Android yet")
    }
}