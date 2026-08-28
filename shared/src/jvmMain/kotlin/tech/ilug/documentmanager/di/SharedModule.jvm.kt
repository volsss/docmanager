package tech.ilug.documentmanager.di

import tech.ilug.documentmanager.export.JvmDocumentProcessor

actual fun getPlatform(): Platform = Platform.JVM

actual fun getDocumentProcessor(): DocumentProcessor = JvmDocumentProcessor()