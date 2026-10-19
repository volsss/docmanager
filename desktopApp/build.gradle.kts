/*
 * Copyright (c) 2026 Andrew Z.
 * docmanager — a scalable document management system.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License v3.0.
 *
 * Distributed WITHOUT ANY WARRANTY. See the GNU General Public License
 * for more details: <https://gnu.org>
 */

import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import java.util.UUID.randomUUID

plugins {
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
}

dependencies {
    implementation(projects.shared)
    implementation(compose.desktop.currentOs)

    implementation(libs.koin.compose)
    implementation(libs.compose.components.resources)
}

compose.desktop {
    application {
        mainClass = "ru.example.docmanager.MainKt"
        jvmArgs += listOf("-Xmx2G")

        buildTypes.release.proguard {
            configurationFiles.from(project.file("proguard-rules.pro"))
            optimize.set(false)
            obfuscate.set(false)
        }

        nativeDistributions {
            packageName = "docmanager"
            packageVersion = "1.1.0"
            description = "A scalable document manager"
            copyright = "Copyright (c) 2026 Andrew Z."
            licenseFile.set(project.file("LICENSE"))

            modules(
                "java.instrument",
                "java.security.jgss",
                "java.sql",
                "java.xml.crypto",
                "jdk.unsupported"
            )

            targetFormats (
                TargetFormat.Dmg,
                TargetFormat.Msi,
                TargetFormat.Rpm,
                TargetFormat.Deb
            )

            appResourcesRootDir.set(
                project.layout.projectDirectory.dir("src/main/resources")
            )

            windows {
                menuGroup = "docmanager"
                upgradeUuid = randomUUID().toString()
                shortcut = true
                iconFile.set(
                    project.layout.projectDirectory.file(
                        "src/main/resources/icon.ico"
                    )
                )
            }
            macOS {
                bundleID = "ru.example.docmanager"
                iconFile.set(
                    project.layout.projectDirectory.file(
                        "src/main/resources/icon.icns"
                    )
                )
            }
            linux {
                iconFile.set(
                    project.layout.projectDirectory.file(
                        "src/main/resources/icon.png"
                    )
                )
            }
        }
    }
}