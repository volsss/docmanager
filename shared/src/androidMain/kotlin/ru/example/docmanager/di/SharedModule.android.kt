package ru.example.docmanager.di

actual fun getPlatform(): Platform = Platform.ANDROID
actual fun getDocumentProcessor(): DocumentProcessor = AndroidDocumentProcessor()