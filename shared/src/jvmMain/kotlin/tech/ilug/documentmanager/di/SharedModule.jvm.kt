package tech.ilug.documentmanager.di

import tech.ilug.documentmanager.export.Test

actual fun getPlatform(): Platform = Platform.JVM

actual fun getDocumentProcessor(): DocumentProcessor = Test()