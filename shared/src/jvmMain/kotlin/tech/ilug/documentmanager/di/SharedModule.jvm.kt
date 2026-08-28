package tech.ilug.documentmanager.di

actual fun getPlatform(): Platform = Platform.JVM

actual fun getDocumentProcessor(): DocumentProcessor = JvmDocumentProcessor()