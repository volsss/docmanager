# Шаг 1. Создание проекта и настройка Gradle

В этом шаге мы создадим новый KMP-проект в **IntelliJ IDEA** и настроим систему сборки с помощью **Gradle** (теория из lab5).

---

## Шаг 1.1. Создание нового проекта

### Создание через мастер IDEA

1. Откройте **IntelliJ IDEA** и выберите **File → New → Project**.
2. На левой панели выберите **Kotlin Multiplatform**.
3. Заполните поля:
   - **Name**: `docmanager`
   - **Package name**: `ru.example.docmanager`
4. Выберите целевые платформы: **Android**, **Desktop**.
5. Убедитесь, что для всех платформ выбран параметр **Share UI** — это означает, что UI-код будет общим (в модуле `shared`), а не дублироваться для каждой платформы.
6. Нажмите **Create**.

Проект будет содержать три модуля:
- **`shared`** — общий код (логика, UI, данные). Компилируется и для JVM, и для Android.
- **`androidApp`** — точка входа для Android (MainActivity, манифест).
- **`desktopApp`** — точка входа для Desktop (JVM) — функция `main()`.

> [!IMPORTANT]
> **Почему Share UI?** Потому что мы используем Compose Multiplatform — один и тот же Compose-код работает и на Android, и на Desktop. Это ключевое преимущество KMP: пишем UI один раз, запускаем везде.

---

## Шаг 1.2. Корневой `build.gradle.kts`

Корневой файл описывает, какие плагины **доступны** в проекте, но не применяет их напрямую. Каждый подмодуль подключает только те плагины, которые ему нужны.

**Файл:** `build.gradle.kts`

```kotlin
plugins {
    // Android-приложение (для модуля androidApp)
    alias(libs.plugins.androidApplication) apply false

    // Kotlin Multiplatform Library для Android (для модуля shared)
    alias(libs.plugins.androidMultiplatformLibrary) apply false

    // Compose Multiplatform (общий UI)
    alias(libs.plugins.composeMultiplatform) apply false

    // Compose Compiler (преобразует @Composable в обычный Kotlin-код)
    alias(libs.plugins.composeCompiler) apply false

    // Kotlin JVM (для модуля desktopApp)
    alias(libs.plugins.kotlinJvm) apply false

    // Kotlin Multiplatform (для модуля shared)
    alias(libs.plugins.kotlinMultiplatform) apply false

    // Kotlin Serialization (для сериализации в навигации)
    alias(libs.plugins.serialization) apply false
}
```

> [!NOTE]
> `apply false` означает: «знай об этом плагине, но не применяй его здесь». Плагины применяются в каждом подмодуле отдельно. `alias(libs.plugins.*)` — это ссылка на Version Catalog (`gradle/libs.versions.toml`), где хранятся все версии.

---

## Шаг 1.3. `settings.gradle.kts`

Этот файл задаёт имя проекта, репозитории для плагинов и зависимостей, а также подключает подмодули.

**Файл:** `settings.gradle.kts`

```kotlin
rootProject.name = "docmanager"

// Типобезопасные аксессоры для модулей: вместо project(":shared") можно написать projects.shared
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    repositories {
        // Google Maven — для Android- и Compose-зависимостей
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        // Центральный Maven-репозиторий
        mavenCentral()
        // Портал Gradle-плагинов
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
    }
}

// Автоматическое разрешение JDK для кроссплатформенной сборки
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

// Подключение модулей проекта
include(":androidApp")
include(":desktopApp")
include(":shared")
```

> [!TIP]
> `TYPESAFE_PROJECT_ACCESSORS` — удобная фича: вместо строковых ссылок `project(":shared")` можно использовать типобезопасные `projects.shared`. Если вы переименуете модуль, компилятор сразу покажет ошибку.

---

## Шаг 1.4. Version Catalog: `gradle/libs.versions.toml`

**Version Catalog** (lab5) — это единый файл, где хранятся все версии и зависимости проекта. Это избавляет от дублирования версий в разных `build.gradle.kts` файлах.

**Файл:** `gradle/libs.versions.toml`

```toml
[versions]
agp = "9.3.3"                                    # Android Gradle Plugin
androidx-lifecycle = "2.11.0"                    # Lifecycle (ViewModel, StateFlow)
android-compileSdk = "37"                        # SDK для компиляции Android
android-minSdk = "26"                            # Минимальный Android (Oreo 8.0)
android-targetSdk = "37"                         # Целевой Android SDK
androidx-activity = "1.13.0"                     # Activity Compose
androidx-material3 = "1.5.0"                     # Material 3 для Android
composeMultiplatform = "1.12.2"                  # Compose Multiplatform
material3 = "1.12.0-alpha03"                     # Material 3 Compose
kotlin = "2.4.21"                                # Kotlin
materialIconsExtended = "1.7.8"                  # Иконки Material
koin = "4.2.2"                                   # Koin DI
exposed = "1.6.1"                                # JetBrains Exposed (SQL)
h2 = "2.5.252"                                   # H2 встраиваемая БД
postgresql = "42.7.14"                           # PostgreSQL драйвер
material3Adaptive = "1.3.0"                      # Адаптивный UI
poi-ooxml = "6.0.0"                              # Apache POI (Word-документы)
multiplatform-nav3-ui = "1.2.0"                  # Навигация Multiplatform
kotlinx-serialization = "1.13.0"                 # Kotlin Serialization

[libraries]
# --- Android ---
androidx-material3 = { module = "androidx.compose.material3:material3", version.ref = "androidx-material3" }
androidx-activity-compose = { module = "androidx.activity:activity-compose", version.ref = "androidx-activity" }

# --- Lifecycle и навигация ---
androidx-lifecycle-viewmodelCompose = { module = "org.jetbrains.androidx.lifecycle:lifecycle-viewmodel-compose", version.ref = "androidx-lifecycle" }
androidx-lifecycle-runtimeCompose = { module = "org.jetbrains.androidx.lifecycle:lifecycle-runtime-compose", version.ref = "androidx-lifecycle" }
androidx-navigation3-ui = { module = "org.jetbrains.androidx.navigation3:navigation3-ui", version.ref = "multiplatform-nav3-ui" }

# --- Compose ---
compose-runtime = { module = "org.jetbrains.compose.runtime:runtime", version.ref = "composeMultiplatform" }
compose-foundation = { module = "org.jetbrains.compose.foundation:foundation", version.ref = "composeMultiplatform" }
compose-material3 = { module = "org.jetbrains.compose.material3:material3", version.ref = "material3" }
compose-material3-adaptive = { module = "org.jetbrains.compose.material3.adaptive:adaptive", version.ref = "material3Adaptive" }
compose-materialIconsExtended = { module = "androidx.compose.material:material-icons-extended", version.ref = "materialIconsExtended" }
compose-ui = { module = "org.jetbrains.compose.ui:ui", version.ref = "composeMultiplatform" }
compose-components-resources = { module = "org.jetbrains.compose.components:components-resources", version.ref = "composeMultiplatform" }
compose-uiToolingPreview = { module = "org.jetbrains.compose.ui:ui-tooling-preview", version.ref = "composeMultiplatform" }
compose-uiTooling = { module = "org.jetbrains.compose.ui:ui-tooling", version.ref = "composeMultiplatform" }

# --- Koin и Serialization ---
koin-core = { module = "io.insert-koin:koin-core", version.ref = "koin" }
koin-compose = { module = "io.insert-koin:koin-compose", version.ref = "koin" }
koin-compose-viewmodel = { module = "io.insert-koin:koin-compose-viewmodel", version.ref = "koin" }
kotlinx-serializationJson = { module = "org.jetbrains.kotlinx:kotlinx-serialization-json", version.ref = "kotlinx-serialization" }

# --- Exposed (SQL ORM) ---
exposed-core = { module = "org.jetbrains.exposed:exposed-core", version.ref = "exposed" }
exposed-dao = { module = "org.jetbrains.exposed:exposed-dao", version.ref = "exposed" }
exposed-jdbc = { module = "org.jetbrains.exposed:exposed-jdbc", version.ref = "exposed" }
exposed-kotlin-datetime = { module = "org.jetbrains.exposed:exposed-kotlin-datetime", version.ref = "exposed" }
exposed-migration-core = { module = "org.jetbrains.exposed:exposed-migration-core", version.ref = "exposed" }
exposed-migration-jdbc = { module = "org.jetbrains.exposed:exposed-migration-jdbc", version.ref = "exposed" }

# --- Базы данных ---
h2 = { module = "com.h2database:h2", version.ref = "h2" }
postgresql = { module = "org.postgresql:postgresql", version.ref = "postgresql" }

# --- Apache POI (Word-документы) ---
poi-ooxml = { module = "org.apache.poi:poi-ooxml", version.ref = "poi-ooxml" }
poi-ooxml-full = { module = "org.apache.poi:poi-ooxml-full", version.ref = "poi-ooxml" }

[plugins]
androidApplication = { id = "com.android.application", version.ref = "agp" }
androidMultiplatformLibrary = { id = "com.android.kotlin.multiplatform.library", version.ref = "agp" }
composeMultiplatform = { id = "org.jetbrains.compose", version.ref = "composeMultiplatform" }
composeCompiler = { id = "org.jetbrains.kotlin.plugin.compose", version.ref = "kotlin" }
kotlinJvm = { id = "org.jetbrains.kotlin.jvm", version.ref = "kotlin" }
kotlinMultiplatform = { id = "org.jetbrains.kotlin.multiplatform", version.ref = "kotlin" }
serialization = { id = "org.jetbrains.kotlin.plugin.serialization", version.ref = "kotlin" }
```

> [!NOTE]
> **Почему Version Catalog?** Без него версии разбросаны по всем `build.gradle.kts` файлам. Обновление Kotlin потребовало бы правки в 3–4 файлах. С Version Catalog — меняете одну строку в `[versions]`, и все зависимости обновляются автоматически. `version.ref` связывает библиотеку с версией из `[versions]`.

---

## Шаг 1.5. Конфигурация `shared/build.gradle.kts`

Модуль `shared` — сердце приложения. Он поддерживает две платформы: **JVM** (Desktop) и **Android**.

**Файл:** `shared/build.gradle.kts`

```kotlin
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    // Kotlin Multiplatform — позволяет компилировать код для нескольких платформ
    alias(libs.plugins.kotlinMultiplatform)

    // Android Multiplatform Library — делает shared-модуль Android-библиотекой
    alias(libs.plugins.androidMultiplatformLibrary)

    // Compose Multiplatform — общий UI для всех платформ
    alias(libs.plugins.composeMultiplatform)

    // Compose Compiler — компиляция @Composable-функций
    alias(libs.plugins.composeCompiler)

    // Serialization — сериализация для навигации
    alias(libs.plugins.serialization)
}

kotlin {
    // Целевая платформа: JVM (Desktop)
    jvm()

    // Целевая платформа: Android
    android {
        namespace = "ru.example.docmanager.shared"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()

        compilerOptions {
            jvmTarget = JvmTarget.JVM_21  // Компилируем в байт-код Java 21
        }
        androidResources {
            enable = true  // Включаем Android-ресурсы
        }
        withHostTest {
            isIncludeAndroidResources = true
        }
    }

    sourceSets {
        // Зависимости для общего кода (commonMain)
        commonMain.dependencies {
            // Compose UI
            implementation(libs.compose.runtime)
            implementation(libs.compose.foundation)
            implementation(libs.compose.material3)
            implementation(libs.compose.material3.adaptive)
            implementation(libs.compose.ui)
            implementation(libs.compose.components.resources)
            implementation(libs.compose.materialIconsExtended)
            implementation(libs.compose.uiToolingPreview)

            // Lifecycle и навигация
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)
            implementation(libs.androidx.navigation3.ui)

            // Koin (DI) и Serialization
            implementation(libs.koin.core)
            implementation(libs.koin.compose)
            implementation(libs.koin.compose.viewmodel)
            implementation(libs.kotlinx.serializationJson)

            // Exposed (SQL ORM)
            implementation(libs.exposed.core)
            implementation(libs.exposed.dao)
            implementation(libs.exposed.jdbc)
            implementation(libs.exposed.kotlin.datetime)
            implementation(libs.exposed.migration.core)
            implementation(libs.exposed.migration.jdbc)

            // Базы данных (H2 для локальной, PostgreSQL для удалённой)
            implementation(libs.h2)
            implementation(libs.postgresql)

            // Apache POI (генерация Word-документов)
            implementation(libs.poi.ooxml)
            implementation(libs.poi.ooxml.full)
        }
    }
}

// Исключаем конфликтующий модуль poi-ooxml-lite
configurations.configureEach {
    exclude(group = "org.apache.poi", module = "poi-ooxml-lite")
}

dependencies {
    androidRuntimeClasspath(libs.compose.uiTooling)
}
```

> [!IMPORTANT]
> **Почему все зависимости в `commonMain`?** Потому что весь код приложения — общий. Ни одна зависимость не привязана к конкретной платформе. Exposed, H2, PostgreSQL, Apache POI — всё это JVM-библиотеки, которые работают и на Android, и на Desktop.

> [!TIP]
> `configurations.configureEach { exclude(...) }` — это необходимо, потому что `poi-ooxml` и `poi-ooxml-full` могут конфликтовать с `poi-ooxml-lite`. Явное исключение предотвращает ошибки сборки.

---

## Шаг 1.6. Ресурсы: строки

Все строковые ресурсы хранятся в XML-файле, который Compose Resources преобразует в типобезопасные ссылки.

**Файл:** `shared/src/commonMain/composeResources/values/strings.xml`

```xml
<resources>
    <!-- Навигация -->
    <string name="route_document_types">Виды документов</string>
    <string name="route_documents">Документы</string>
    <string name="route_form">Форма</string>
    <string name="route_home">Главная</string>
    <string name="route_reference">Справочник</string>
    <string name="route_references">Справочники</string>
    <string name="route_settings">Настройки</string>

    <!-- Типы документов -->
    <string name="document_power_of_attorney">Доверенность</string>
    <string name="document_power_of_attorney_resource">Доверенность.docx</string>
    <string name="document_test">Тестовый документ</string>

    <!-- Справочники -->
    <string name="reference_individuals">Физические лица</string>
    <string name="reference_organizations">Организации</string>
    <string name="reference_products">Материальные ценности</string>
    <string name="reference_suppliers">Поставщики</string>

    <!-- Форма -->
    <string name="form_saved">Форма сохранена</string>
    <string name="form_deleted">Форма удалена</string>
    <string name="form_error_empty_required">Заполните все обязательные поля</string>
    <string name="form_section_header">Шапка документа</string>
    <string name="form_section_body">Тело документа</string>
    <string name="form_button_save">Сохранить</string>
    <string name="form_button_save_as_new">Сохранить как новый</string>
    <string name="form_button_delete">Удалить</string>
    <string name="form_button_export">Экспорт в файл</string>
    <string name="form_button_print">Печать</string>
    <string name="form_field_organization">Организация</string>
    <string name="form_field_individual">Физическое лицо</string>
    <string name="form_field_product">Материальная ценность</string>
    <string name="form_field_supplier">Поставщик</string>

    <!-- Поля шапки -->
    <string name="header_field_number">Номер документа</string>
    <string name="header_field_discharge_date">Дата</string>
    <string name="header_field_end_date">Дата окончания</string>
    <string name="header_field_supplier_agreement">Договор поставки</string>
    <!-- ... и другие поля ... -->

    <!-- Поля тела -->
    <string name="body_field_number_sorted">Номер по порядку</string>
    <string name="body_field_products">Материальные ценности</string>
    <string name="body_field_unit">Единица измерения</string>
    <string name="body_field_count">Количество</string>
</resources>
```

> [!NOTE]
> **Почему XML?** Compose Multiplatform использует тот же формат ресурсов, что и Android. При компиляции генерируется класс `Res`, через который доступ к строкам становится типобезопасным: `Res.string.document_power_of_attorney`. Это `StringResource` — не просто строка, а ссылка, которую можно передать в `stringResource()` для получения локализованного текста.

---

## Шаг 1.7. Шаблон документа

Для генерации Word-документов нам нужен шаблон `.docx` с плейсхолдерами.

**Файл:** `shared/src/commonMain/resources/Доверенность.docx`

Шаблон создаётся в Microsoft Word (или LibreOffice) и содержит:
- Плейсхолдеры в формате `{{number}}`, `{{dischargeDate}}`, `{{organizationName}}` и т.д.
- Таблицу с заголовками: «Номер по порядку», «Материальные ценности», «Единица измерения», «Количество».

> [!IMPORTANT]
> Плейсхолдеры должны точно соответствовать значениям `placeholder` в `HeaderFieldType`. Например, `{{number}}` соответствует `HeaderFieldType.NUMBER`.
