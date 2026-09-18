package ru.example.docmanager.di

actual fun getPlatform(): Platform = Platform.JVM

actual fun getDocumentProcessor(): DocumentProcessor = JvmDocumentProcessor()