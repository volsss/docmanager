/*
 * Copyright (c) 2026 Andrew Z.
 * docmanager — a scalable document management system.
 */

import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidMultiplatformLibrary)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.serialization)
}

kotlin {
    jvm()

    android {
        namespace = "ru.example.docmanager.shared"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()

        compilerOptions {
            jvmTarget = JvmTarget.JVM_25
        }
        androidResources {
            enable = true
        }
        withHostTest {
            isIncludeAndroidResources = true
        }
    }

    sourceSets {
        commonMain.dependencies {
            // Compose
            implementation(libs.compose.runtime)
            implementation(libs.compose.foundation)
            implementation(libs.compose.material3)
            implementation(libs.compose.material3.adaptive)
            implementation(libs.compose.ui)
            implementation(libs.compose.components.resources)
            implementation(libs.compose.materialIconsExtended)
            implementation(libs.compose.uiToolingPreview)

            // Lifecycle and navigation
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)
            implementation(libs.androidx.navigation3.ui)

            // Koin and Serialization
            implementation(libs.koin.core)
            implementation(libs.koin.compose)
            implementation(libs.koin.compose.viewmodel)
            implementation(libs.kotlinx.serializationJson)

            // Exposed
            implementation(libs.exposed.core)
            implementation(libs.exposed.dao)
            implementation(libs.exposed.jdbc)
            implementation(libs.exposed.javatime)
            implementation(libs.exposed.migration.core)
            implementation(libs.exposed.migration.jdbc)

            // Databases
            implementation(libs.h2)
            implementation(libs.postgresql)

            // Document processor
            implementation(libs.poi.ooxml)
        }
        androidMain.dependencies {
            implementation(libs.androidx.material3)
        }
    }
}

dependencies {
    androidRuntimeClasspath(libs.compose.uiTooling)
}
