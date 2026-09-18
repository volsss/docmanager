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

---

### Литература

- [Kotlin](https://kotlinlang.org/docs/getting-started.html)
- [Kotlin Multiplatform Development](https://www.jetbrains.com/help/kotlin-multiplatform-dev/get-started.html)
- [Jetpack Compose](https://developer.android.com/develop/ui/compose/documentation)
- [PostgreSQL](https://www.postgresql.org/docs/)
- [H2](https://h2database.com/html/quickstart.html)