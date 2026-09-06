import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidMultiplatformLibrary)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.serialization)
}

kotlin {
    jvm ()

    compilerOptions {
        freeCompilerArgs.add("-Xexplicit-backing-fields")
    }

    android {
       namespace = "tech.ilug.documentmanager.shared"
       compileSdk = libs.versions.android.compileSdk.get().toInt()
       minSdk = libs.versions.android.minSdk.get().toInt()
    
       compilerOptions {
           jvmTarget = JvmTarget.JVM_11
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
            implementation(libs.compose.runtime)
            implementation(libs.compose.foundation)
            implementation(libs.compose.material3)
            implementation(libs.compose.material3.adaptive)
            implementation(libs.compose.ui)
            implementation(libs.compose.components.resources)
            implementation(libs.compose.materialIconsExtended)
            implementation(libs.compose.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)
            implementation(libs.koin.compose)

            implementation(libs.exposed.core)
            implementation(libs.exposed.dao)
            implementation(libs.exposed.jdbc)
            implementation(libs.exposed.javatime)
            implementation(libs.exposed.migration.core)
            implementation(libs.exposed.migration.jdbc)
            implementation(libs.kotlinx.serializationJson)
        }
        androidMain.dependencies {
            implementation(libs.hikaricp)
            implementation(libs.h2)
            implementation(libs.compose.uiToolingPreview)
            implementation(libs.poi.ooxml)
            implementation(libs.koin.core)
        }
        jvmMain.dependencies {
            implementation(libs.hikaricp)
            implementation(libs.h2)
            implementation(libs.poi.ooxml)

            implementation(libs.koin.core)
        }
    }
}

dependencies {
    androidRuntimeClasspath(libs.compose.uiTooling)
}