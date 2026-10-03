# docmanager

Это Kotlin Multiplatform проект таргетированный на Android, Desktop (JVM).

* [/shared](./shared/src) папка для кода, который будет использоваться совместно в ваших многоплатформенных приложениях Compose.
  Она содержит несколько подпапок:
  - [commonMain](./shared/src/commonMain/kotlin) относится к коду, который будет использоваться на всех таргетированных платформах.
  - Другие папки предназначены для кода Kotlin, который будет скомпилирован только для платформы, указанной в имени папки.
    Например, если вы хотите отредактировать часть, специфичную для настольных компьютеров (JVM) папка [jvmMain](./shared/src/jvmMain/kotlin)
    будет подходящим местом. Аналогично, если вы хотите отредактировать часть, специфичную для Android, папка [androidMain](./shared/src/androidMain/kotlin)
    будет подходящим местом.

### Запуск приложений

Используйте конфигурации запуска в виджетах, предоставленных в вашей IDE. 
Вы также можете использовать эти команды:

- Android приложение: `./gradlew :androidApp:assembleDebug`
- Desktop приложение:
  - Hot reload: `./gradlew :desktopApp:hotRun --auto`
  - Обычное: `./gradlew :desktopApp:run`

## Документация

### Kotlin:
- [X] **I. Первая часть**
- - [X] Точка входа
- - [X] Скрипты
- - [X] Комментарии
- - [X] Переменные
- - [X] Типы данных
- - [X] Строки
- - [X] I/O
- - [X] Операторы
- - [X] Условные операторы
- - [X] Диапазоны
- - [X] Циклы
- - [X] Исключения
- [X] **II. Вторая часть**
- - [X] Функции
- - [X] Null safety
- - [X] Лямбда-выражения
- - [X] Тип функции
- - [X] Функции высшего порядка
- - [X] Trailing lambda
- [X] **III. Третья часть**
- - [X] Классы
- - [X] Инкапсуляция
- - [X] Полиморфизм
- - [X] Коллекции
- - [X] Перебор коллекций
- - [X] Read-only и Mutable
- [X] **IV. Четвёртая часть**
- - [X] Коллекции и лямбды
- - [X] Неиспользуемые параметры
- - [X] Деструктуризация
- - [X] Function references
- - [X] Цепочки операций
- - [X] Any
- - [X] Extension functions
- - [X] Extension functions
- [X] **V. Пятая часть**
- - [X] enum class
- - [X] sealed class и sealed interface
- - [X] Nothing
- - [X] Generics
- - [X] object
- - [X] companion object
- - [X] operator
- - [X] Делегирование

### Gradle:
- [ ] **I. Первая часть**
- [ ] **II. Вторая часть**

### Coroutines
- [X] suspend
- [X] runBlocking
- [X] launch
- [X] Job
- [X] async
- [X] CoroutineScope
- [X] Dispatchers
- [X] try-catch
- [X] supervisorScope

### Android:
- [ ] **I. Первая часть**
- [ ] **II. Вторая часть**

### Compose:
- [ ] **I. Первая часть**
- [ ] **II. Вторая часть**

### docmanager App:
- [ ] **I. Первая часть**
- [ ] **II. Вторая часть**
- [ ] **III. Третья часть**
- [ ] **IV. Четвёртая часть**
- [ ] **V. Пятая часть**
- [ ] **VI. Шестая часть**

---

### Литература

- [Kotlin](https://kotlinlang.org/docs/getting-started.html)
- [Kotlin Multiplatform Development](https://www.jetbrains.com/help/kotlin-multiplatform-dev/get-started.html)
- [Jetpack Compose](https://developer.android.com/develop/ui/compose/documentation)
- [PostgreSQL](https://www.postgresql.org/docs/)
- [H2](https://h2database.com/html/quickstart.html)