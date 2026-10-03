# Теория Gradle: Часть 1

## Packages

По мере роста программы количество файлов увеличивается.

Чтобы организовать код, используются пакеты (`package`).

Например:

```mermaid
treeView-beta
    my-project/
        src/main/kotlin/
            org/example/
                Main.kt
                User.kt
                Calculator.kt
```

В файле `User.kt` можно указать:

```kotlin
package org.example
```

Например:

```kotlin
package org.example

class User(
    val name: String
)
```

Теперь класс `User` находится в пакете `org.example`

Пакет является способом логически группировать код. **Kotlin** не требует, 
чтобы путь к файлу строго совпадал с именем пакета, однако рекомендуется 
придерживаться соответствующей структуре каталогов.

## Imports

Если код находится в другом пакете, для использования его сущностей 
применяется `import`.

Например, есть файл:

```kotlin
package org.example.model

class User(
    val name: String
)
```

В другом файле:

```kotlin
package org.example.app

import org.example.model.User

fun main() {
    val user = User("Alex")

    println(user.name)
}
```

`import` делает класс `User` доступным в текущем файле.

Импортировать можно не только классы, но также функции, свойства, 
объекты, enum-константы и другие сущности.

## Структура Kotlin-проекта

До этого мы в основном работали с отдельными `.kt` файлами.

Реальный проект обычно содержит значительно больше файлов.

Простейший **Kotlin/JVM**-проект с **Gradle** может выглядеть так:

```mermaid
---
config:
    treeView:
        showIcons: true
---
treeView-beta
    my-project/
        🛠️ gradlew ## Инструмент Gradle Wrapper для Linux и macOS
        🛠️ gradlew.bat ## Инструмент Gradle Wrapper для Windows
        ⚙️ settings.gradle.kts ## описывает структуру Gradle build
        ⚙️ build.gradle.kts ## Скрипт сборки Gradle
        ⚙️ gradle.properties ## Конфигурация Gradle
        gradle/
            wrapper/
                🛠️ gradle-wrapper.jar ## Сам Gradle Wrapper
                ⚙️ gradle-wrapper.properties ## Конфигурация Gradle Wrapper
        app/
            ⚙️ build.gradle.kts ## Скрипт сборки Gradle для модуля app/
            src/
                main/kotlin/ ## Исходный Kotlin-код
                    org/example/
                        Main.kt :::highlight ## Точка входа
                    resources/
                test/kotlin/ ## Тестовый Kotlin-код
                    org/example/
                        🧪 MainTest.kt ## Точка входа для тестов
```

Такая структура основана на стандартной модели **Gradle**-проектов и 
**Kotlin/JVM**-примеров. `settings.gradle.kts` задаёт структуру build, 
`build.gradle.kts` содержит build-конфигурацию, а исходный **Kotlin**-код 
находится в `source set` проекта.

Разберём основные элементы.