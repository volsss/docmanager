package tech.ilug.documentmanager.di

actual fun getPlatform(): Platform = Platform.ANDROID
actual fun getDocumentProcessor(): DocumentProcessor = AndroidDocumentProcessor()