# Шаг 8. Платформенные модули и запуск

Каждая платформа имеет свою точку входа — код, который запускает приложение. Общий код (в `shared/commonMain`) не знает о платформенных деталях, а платформенный код (`jvmMain`, `androidMain`) подключает нужные реализации через `expect/actual`.

---

## Шаг 8.1. Точка входа для Desktop

**Файл:** `desktopApp/src/main/kotlin/ru/example/docmanager/main.kt`

```kotlin
package ru.example.docmanager

import androidx.compose.material3.MaterialExpressiveTheme
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import docmanager.desktopapp.generated.resources.Res
import docmanager.desktopapp.generated.resources.icon
import org.jetbrains.compose.resources.painterResource
import org.koin.core.context.GlobalContext.startKoin
import ru.example.docmanager.di.sharedModule
import ru.example.docmanager.ui.App
import ru.example.docmanager.ui.theme.rememberAppColorScheme

fun main() {
    // 1. Инициализация Koin — регистрация всех зависимостей
    startKoin {
        modules(sharedModule)
    }

    // 2. Запуск Compose-приложения
    application {
        MaterialExpressiveTheme(
            colorScheme = rememberAppColorScheme()
        ) {
            Window(
                onCloseRequest = ::exitApplication,
                state = rememberWindowState(
                    position = WindowPosition.Aligned(Alignment.Center),
                    width = 1300.dp,
                    height = 800.dp
                ),
                title = "docmanager",
                icon = painterResource(Res.drawable.icon)
            ) {
                App()  // Главный компонент приложения
            }
        }
    }
}
```

### Пошаговый разбор

1. **`startKoin { modules(sharedModule) }`** — инициализирует Koin и регистрирует все модули: data, repository, viewModel, platform. Без этого приложение не сможет создать ViewModel.
2. **`application { }`** — блок Compose для Desktop. Создаёт окно приложения.
3. **`MaterialExpressiveTheme`** — задаёт цветовую схему и типографику Material Design 3.
4. **`Window`** — создаёт окно с заголовком, иконкой и размером.
5. **`App()`** — вызывает главный Compose-компонент, который показывает `ConnectionScreen` или `Router`.

---

## Шаг 8.2. Тема приложения

**Файл:** `shared/src/commonMain/kotlin/.../ui/theme/AppColorScheme.kt`

```kotlin
package ru.example.docmanager.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

fun defaultColorScheme(isDark: Boolean): ColorScheme {
    return if (isDark) {
        darkColorScheme(
            primary = Color(0xFFB8C4FF),
            onPrimary = Color(0xFF1F2C61),
            primaryContainer = Color(0xFF374379),
            onPrimaryContainer = Color(0xFFDDE1FF),
            secondary = Color(0xFFC2C5DD),
            onSecondary = Color(0xFF2C2F42),
            secondaryContainer = Color(0xFF424659),
            onSecondaryContainer = Color(0xFFDEE1F9),
            tertiary = Color(0xFFE4BAD9),
            onTertiary = Color(0xFF43273F),
            tertiaryContainer = Color(0xFF5C3D56),
            onTertiaryContainer = Color(0xFFFFD7F4),
            error = Color(0xFFFFB4AB),
            onError = Color(0xFF690005),
            errorContainer = Color(0xFF93000A),
            onErrorContainer = Color(0xFFFFDAD6),
            background = Color(0xFF121318),
            onBackground = Color(0xFFE3E1E9),
            surface = Color(0xFF121318),
            onSurface = Color(0xFFE3E1E9),
            surfaceVariant = Color(0xFF45464F),
            onSurfaceVariant = Color(0xFFC6C5D0),
            outline = Color(0xFF90909A),
            outlineVariant = Color(0xFF45464F),
            scrim = Color(0xFF000000),
            inverseSurface = Color(0xFFE3E1E9),
            inverseOnSurface = Color(0xFF2F3036),
            inversePrimary = Color(0xFF4F5B92),
            surfaceDim = Color(0xFF121318),
            surfaceBright = Color(0xFF38393F),
            surfaceContainerLowest = Color(0xFF0D0E13),
            surfaceContainerLow = Color(0xFF1B1B21),
            surfaceContainer = Color(0xFF1F1F25),
            surfaceContainerHigh = Color(0xFF292A2F),
            surfaceContainerHighest = Color(0xFF34343A),
        )
    } else {
        lightColorScheme(
            primary = Color(0xFF4F5B92),
            onPrimary = Color(0xFFFFFFFF),
            primaryContainer = Color(0xFFDDE1FF),
            onPrimaryContainer = Color(0xFF374379),
            secondary = Color(0xFF5A5D72),
            onSecondary = Color(0xFFFFFFFF),
            secondaryContainer = Color(0xFFDEE1F9),
            onSecondaryContainer = Color(0xFF424659),
            tertiary = Color(0xFF75546E),
            onTertiary = Color(0xFFFFFFFF),
            tertiaryContainer = Color(0xFFFFD7F4),
            onTertiaryContainer = Color(0xFF5C3D56),
            error = Color(0xFFBA1A1A),
            onError = Color(0xFFFFFFFF),
            errorContainer = Color(0xFFFFDAD6),
            onErrorContainer = Color(0xFF93000A),
            background = Color(0xFFFBF8FF),
            onBackground = Color(0xFF1B1B21),
            surface = Color(0xFFFBF8FF),
            onSurface = Color(0xFF1B1B21),
            surfaceVariant = Color(0xFFE2E1EC),
            onSurfaceVariant = Color(0xFF45464F),
            outline = Color(0xFF767680),
            outlineVariant = Color(0xFFC6C5D0),
            scrim = Color(0xFF000000),
            inverseSurface = Color(0xFF2F3036),
            inverseOnSurface = Color(0xFFF2F0F7),
            inversePrimary = Color(0xFFB8C4FF),
            surfaceDim = Color(0xFFDBD9E0),
            surfaceBright = Color(0xFFFBF8FF),
            surfaceContainerLowest = Color(0xFFFFFFFF),
            surfaceContainerLow = Color(0xFFF5F2FA),
            surfaceContainer = Color(0xFFEFEDF4),
            surfaceContainerHigh = Color(0xFFE9E7EF),
            surfaceContainerHighest = Color(0xFFE3E1E9),
        )
    }
}
```

> [!NOTE]
> Цветовая схема определяет все цвета приложения: кнопки, фон, текст, иконки. Material Design 3 использует систему `primary`, `secondary`, `tertiary` и их контейнеры.

---

## Шаг 8.3. expect/actual для платформенного кода

Механизм `expect/actual` позволяет определить общий интерфейс в `commonMain` и платформенную реализацию в `jvmMain`/`androidMain`.

### Пример: URL подключения к БД

**Общий (commonMain):**
```kotlin
package ru.example.docmanager.data.database

expect val defaultH2JdbcUrl: String
expect val defaultPostgresJdbcUrl: String
```

**JVM (jvmMain):**
```kotlin
package ru.example.docmanager.data.database

actual val defaultH2JdbcUrl = "jdbc:h2:~/ru.example.docmanager/new_project;MODE=MySQL"
actual val defaultPostgresJdbcUrl = "jdbc:postgresql://localhost:5432/new_project"
```

**Android (androidMain):**
```kotlin
package ru.example.docmanager.data.database

actual val defaultH2JdbcUrl = "jdbc:h2:/data/data/ru.example.docmanager/new_project;MODE=MySQL"
actual val defaultPostgresJdbcUrl = "jdbc:postgresql://localhost:5432/new_project"
```

### Пример: DocumentProcessor

**Общий (commonMain):**
```kotlin
package ru.example.docmanager.domain.document

interface DocumentProcessor {
    suspend fun processSave(...)
    suspend fun processPrint(...)
}
```

**JVM (jvmMain):**
```kotlin
package ru.example.docmanager.di

import org.koin.dsl.module
import ru.example.docmanager.data.document.JvmDocumentProcessor
import ru.example.docmanager.domain.document.DocumentProcessor

actual val platformModule = module {
    single<DocumentProcessor> { JvmDocumentProcessor() }
}
```

**Android (androidMain):**
```kotlin
package ru.example.docmanager.di

import org.koin.dsl.module
import ru.example.docmanager.data.document.AndroidDocumentProcessor
import ru.example.docmanager.domain.document.DocumentProcessor

actual val platformModule = module {
    single<DocumentProcessor> { AndroidDocumentProcessor() }
}
```

> [!IMPORTANT]
> **Почему `expect/actual`?** Потому что путь к файлам и API для работы с файлами различаются на Desktop и Android. Общий код не должен знать о этих различиях — он работает с абстракциями.

---

## Шаг 8.4. Запуск и сборка

### Запуск Desktop-приложения

1. В IntelliJ IDEA выберите конфигурацию `desktopApp`.
2. Нажмите **Run**.
3. Откроется окно приложения с экраном подключения к БД.
4. Выберите **Локально** (H2) и нажмите **Подключиться**.
5. Приложение перейдёт на главный экран.

### Сборка Desktop-приложения

```bash
# Windows
.\gradlew.bat package

# Linux/Mac
./gradlew package
```

Результат: исполняемый файл в `desktopApp/build/compose/jvm/bin/`.

### Запуск Android-приложения

1. Подключите Android-устройство или запустите эмулятор.
2. Выберите конфигурацию `androidApp`.
3. Нажмите **Run**.

---

## Шаг 8.5. Полный поток работы приложения

```
1. main() → startKoin { modules(sharedModule) }
   ↓
2. App() → ConnectionScreen (подключение к БД)
   ↓
3. DatabaseManager.init() → создание таблиц через Exposed Migration
   ↓
4. Router() → StartScreen (главная страница)
   ↓
5. Навигация → DocumentTypesScreen → DocumentsScreen
   ↓
6. Создание документа → FormScreen → PowerOfAttorneyScreen
   ↓
7. PowerOfAttorneyViewModel.onInit() → параллельная загрузка справочников
   ↓
8. Заполнение формы → onSaveDocument() → repository.createDocument()
   ↓
9. Экспорт → documentProcessor.processSave() → DocumentEngine → .docx файл
```

---

**Следующий шаг:** [Практическое задание](practice.md) — добавьте новый тип документа.
