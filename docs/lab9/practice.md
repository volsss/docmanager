# Практическое задание

## Задание: Добавить новый тип документа «Акт приёмки»

На основе изученного в lab9 создайте новый тип документа **«Акт приёмки»** — документ, который фиксирует приёмку материальных ценностей.

### Что нужно сделать

#### 1. Доменный слой

- Добавьте новый элемент в `enum class DocumentType`:
  ```kotlin
  ACCEPTANCE_ACT(
      stringResource = Res.string.document_acceptance_act,
      icon = Icons.Rounded.CheckCircle
  )
  ```
- Добавьте новые поля в `HeaderFieldType` (если нужно).
- Добавьте новые поля в `BodyFieldType` (если нужно).

#### 2. Слой данных

- Создайте модель `AcceptanceAct` (аналог `PowerOfAttorney`).
- Создайте модель `AcceptanceActHeader` (аналог `PowerOfAttorneyHeader`).
- Создайте модель `AcceptanceActBody` и `AcceptanceActBodyItem`.
- Создайте таблицы Exposed: `AcceptanceActHeadersTable`, `AcceptanceActBodiesTable`.
- Создайте репозиторий `AcceptanceActRepository`.
- Добавьте `AcceptanceActRepository` в `RepositoryModule`.

#### 3. UI-слой

- Создайте `AcceptanceActScreen` (аналог `PowerOfAttorneyScreen`).
- Добавьте маршрут в `Route.kt` (если нужно отдельный).
- Обновите `FormScreen`, чтобы он обрабатывал `DocumentType.ACCEPTANCE_ACT`.

#### 4. ViewModel

- Создайте `AcceptanceActUiState` (аналог `PowerOfAttorneyUiState`).
- Создайте `AcceptanceActViewModel` (аналог `PowerOfAttorneyViewModel`).
- Зарегистрируйте ViewModel в `ViewModelModule`.

#### 5. Ресурсы

- Добавьте строки в `strings.xml`:
  ```xml
  <string name="document_acceptance_act">Акт приёмки</string>
  <string name="document_acceptance_act_resource">Акт приёмки.docx</string>
  ```
- Создайте шаблон `Акт приёмки.docx` с плейсхолдерами.

#### 6. DatabaseManager

- Добавьте новые таблицы в список миграций `DefaultDatabaseManager`.

---

### Структура «Акта приёмки»

**Шапка:**
- Номер акта
- Дата акта
- Организация (из справочника)
- Поставщик (из справочника)

**Тело (таблица):**
- № по порядку
- Наименование ценности (из справочника продуктов)
- Количество
- Единица измерения
- Примечание

---

### Критерии оценки

| Критерий | Баллы |
|---|---|
| Доменный слой: `DocumentType`, `HeaderFieldType` | 1 |
| Модели данных: `AcceptanceAct`, `AcceptanceActHeader`, `AcceptanceActBody` | 1 |
| Таблицы Exposed с правильными foreign key | 1 |
| Репозиторий с CRUD-операциями | 2 |
| DI: регистрация в Koin | 1 |
| UI: экран формы с компонентами | 1 |
| ViewModel: StateFlow, обработка событий | 1 |
| Ресурсы: строки, шаблон .docx | 1 |
| Экспорт в Word работает корректно | 1 |
| **Итого** | **10** |

---

### Подсказки

1. **Используйте существующий код как шаблон.** Скопируйте `PowerOfAttorney*` файлы и адаптируйте их под «Акт приёмки».
2. **Не дублируйте лишнее.** Если «Акт приёмки» использует те же справочники (Organization, Supplier, Product), подключите их через те же репозитории.
3. **Тестируйте пошагово.** Сначала убедитесь, что репозиторий работает, затем — что ViewModel загружает данные, и только потом — что UI отображает форму.
4. **Шаблон .docx.** Создайте простой шаблон в Word с плейсхолдерами `{{number}}`, `{{dischargeDate}}`, `{{organizationName}}` и таблицей с заголовками.

---

### Дополнительные задания (бонус)

- Добавьте валидацию: номер акта не может быть отрицательным.
- Добавьте поиск по актам (по номеру, дате, организации).
- Добавьте массовый экспорт: выбрать несколько актов и экспортировать в один .docx-файл.
- Добавьте тёмную тему: переключатель в настройках.
