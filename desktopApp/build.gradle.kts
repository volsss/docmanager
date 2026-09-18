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
    implementation(libs.compose.components.resources)
    implementation(libs.kotlinx.coroutinesSwing)
    implementation(libs.koin.compose)

    implementation(libs.compose.uiToolingPreview)
}

compose.desktop {
    application {
        mainClass = "ru.example.docmanager.MainKt"

        buildTypes.release.proguard {
            configurationFiles.from(project.file("proguard-rules.pro"))
            optimize.set(false)
            obfuscate.set(false)
        }

        nativeDistributions {
            modules("java.sql", "java.naming")

            targetFormats (
                TargetFormat.Dmg,
                TargetFormat.Msi,
                TargetFormat.Exe,
                TargetFormat.Deb
            )
            packageName = "docmanager"
            packageVersion = "1.1.0"

            description = "A scalable document manager"

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
                minimumSystemVersion = "12.0"
                signing {
                    sign.set(true)
                    prefix.set("ru.example.docmanager")
                    identity.set("-")
                }
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