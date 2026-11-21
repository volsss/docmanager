# Лабораторная работа 9:<br/>Создание KMP-приложения docmanager: пример «Доверенности»

В этой лабораторной работе мы соберём все знания из предыдущих работ (lab1–lab8) и создадим полноценное кроссплатформенное приложение **docmanager** — систему управления документами на основе **Kotlin Multiplatform** и **Compose Multiplatform**.

В качестве примера предметной области мы реализуем документ **«Доверенность»** — юридический документ, который содержит:
- **Шапку** (header) — реквизиты: номер, даты, организация, физическое лицо, поставщик, договор.
- **Тело** (body) — таблицу с позициями: материал, единица измерения, количество.

## Что мы используем

| Лабораторная | Тема | Где применяется в docmanager |
|---|---|---|
| lab1 | Установка IDE | IntelliJ IDEA как среда разработки |
| lab2 | Основы Kotlin | Переменные, функции, классы, data class, интерфейсы |
| lab3 | Классы и коллекции | Модели данных, репозитории, обработка списков |
| lab4 | Enum, sealed, generics | `DocumentType`, `HeaderFieldType`, обобщённые репозитории |
| lab5 | Gradle | Конфигурация модулей, зависимости, Version Catalog |
| lab6 | Корутин | Асинхронные запросы к БД в ViewModel |
| lab7 | Jetpack Compose | Весь UI: экраны, компоненты, состояние, навигация |
| lab8 | KMP и CMP | Архитектура проекта, общие и платформенные модули |

## Архитектура приложения

```
docmanager/
├── shared/                          # Общий модуль (KMP)
│   └── commonMain/kotlin/
│       ├── domain/                  # Доменный слой (интерфейсы, сущности)
│       │   ├── document/            #   Document, DocumentType, DocumentEngine
│       │   └── reference/           #   Reference, ReferenceType
│       ├── data/                    # Слой данных (модели, репозитории, БД)
│       │   ├── database/            #   Таблицы Exposed, DatabaseManager
│       │   ├── document/            #   PowerOfAttorney и др.
│       │   ├── reference/           #   Organization, Individual и др.
│       │   └── repositories/        #   CRUD-репозитории
│       ├── ui/                      # UI-слой (Compose)
│       │   ├── components/          #   TextInput, DateInput, ReferenceDropdown
│       │   ├── router/              #   Навигация (Route, Router)
│       │   ├── screens/             #   Экраны приложения
│       │   ├── theme/               #   Цветовая схема
│       │   ├── utils/               #   Утилиты (даты, адаптивность)
│       │   └── viewmodel/           #   ViewModel (MVVM)
│       └── di/                      # Внедрение зависимостей (Koin)
│           ├── DataModule.kt
│           ├── RepositoryModule.kt
│           ├── SharedModule.kt
│           └── ViewModelModule.kt
├── shared/src/jvmMain/              # Платформенный код для Desktop
├── shared/src/androidMain/          # Платформенный код для Android
├── desktopApp/                      # Точка входа для Desktop (JVM)
└── androidApp/                      # Точка входа для Android
```

## Цель работы

Научиться создавать полноценное KMP-приложение с нуля, применяя:
- **MVVM-архитектуру** — разделение на ViewModel, UI и данные.
- **Базы данных** — JetBrains Exposed для работы с SQL.
- **Внедрение зависимостей** — Koin для связывания слоёв.
- **Compose Multiplatform** — единый UI для Desktop и Android.
- **Экспорт документов** — Apache POI для генерации .docx-файлов из шаблонов.

## Структура руководства

Каждый шаг содержит **полный код** каждого файла, подробное описание решений и объяснение, **почему** сделано именно так.

1. [Создание проекта и настройка Gradle](step1-project-setup.md)
2. [Доменный слой](step2-domain-layer.md)
3. [Слой данных](step3-data-layer.md)
4. [Внедрение зависимостей](step4-di-layer.md)
5. [UI-слой: компоненты и навигация](step5-ui-layer.md)
6. [ViewModel и состояние](step6-viewmodel.md)
7. [Обработка документов](step7-document-processing.md)
8. [Платформенные модули и запуск](step8-platform-modules.md)
9. [Практическое задание](practice.md)
