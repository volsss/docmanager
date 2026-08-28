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

kotlin {
    compilerOptions {
        freeCompilerArgs.add("-Xexplicit-backing-fields")
    }
}

compose.desktop {
    application {
        mainClass = "tech.ilug.documentmanager.MainKt"

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
                TargetFormat.Deb
            )
            packageName = "DocumentManager"
            packageVersion = "1.0.0"

            vendor = "ILUG"
            description = "A scalable document manager for power of attorney"

            appResourcesRootDir.set(
                project.layout.projectDirectory.dir("src/main/resources")
            )

            windows {
                menuGroup = "DocumentManager"
                upgradeUuid = randomUUID().toString()
                shortcut = true
            }
            macOS {
                bundleID = this@nativeDistributions.packageName
            }
        }
    }
}