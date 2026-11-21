# Шаг 2. Доменный слой

Доменный слой (lab3, lab4) — это ядро бизнес-логики. Здесь мы определяем абстракции, которые **не зависят** от базы данных, UI или внешних библиотек. Доменный слой задаёт контракт: «что такое документ», «что такое справочник», но не описывает «как хранить» или «как отображать».

---

## Шаг 2.1. Интерфейс документа

Каждый документ в системе реализует общий интерфейс `Document`. Это позволяет работать с любыми типами документов единообразно.

**Файл:** `shared/src/commonMain/kotlin/.../domain/document/Document.kt`

```kotlin
package ru.example.docmanager.domain.document

import ru.example.docmanager.domain.document.header.DocumentHeader

interface Document {
    val type: DocumentType
    val header: DocumentHeader
}
```

### Почему интерфейс, а не абстрактный класс?

- **Интерфейс** позволяет реализовать несколько контрактов одновременно. Например, `PowerOfAttorney` реализует и `Document`, и `WithBody`.
- **Не привязывает к иерархии наследования.** Любой класс может реализовать `Document`, даже если он уже наследуется от другого класса.
- **Чистая абстракция.** Доменный слой не знает о базе данных — он только говорит: «у документа есть тип и шапка».

---

## Шаг 2.2. Типы документов

Типы документов описаны `enum class` (lab4). Каждый тип привязан к строковому ресурсу (для отображения названия) и иконке (для визуального различения).

**Файл:** `shared/src/commonMain/kotlin/.../domain/document/DocumentType.kt`

```kotlin
package ru.example.docmanager.domain.document

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Description
import androidx.compose.material.icons.rounded.TableView
import androidx.compose.ui.graphics.vector.ImageVector
import docmanager.shared.generated.resources.Res
import docmanager.shared.generated.resources.document_power_of_attorney
import docmanager.shared.generated.resources.document_test
import org.jetbrains.compose.resources.StringResource

enum class DocumentType(
    val stringResource: StringResource,
    val icon: ImageVector
) {
    POWER_OF_ATTORNEY(
        stringResource = Res.string.document_power_of_attorney,
        icon = Icons.Rounded.TableView
    ),
    TEST(
        stringResource = Res.string.document_test,
        icon = Icons.Rounded.Description
    )
}
```

### Почему `enum class` с параметрами?

- **Исчерпывающий список.** Компилятор знает все возможные типы документов. В `when` можно использовать exhaustive-проверку без `else`.
- **Каждый тип несёт данные.** `stringResource` — для локализации названия, `icon` — для отображения в списке.
- **Типобезопасность.** Нельзя передать случайную строку — только значение из enum.

> [!TIP]
> `Icons.Rounded.TableView` — иконка из Material Icons Extended. Она показывает, что документ содержит таблицу (тело с позициями).

---

## Шаг 2.3. Шапка документа

Шапка документа содержит общие реквизиты: номер, дату и метод `toMap()` для преобразования в карту плейсхолдеров.

**Файл:** `shared/src/commonMain/kotlin/.../domain/document/header/DocumentHeader.kt`

```kotlin
package ru.example.docmanager.domain.document.header

import kotlinx.datetime.LocalDate

interface DocumentHeader {
    val id: Int
    var number: Int
    var dischargeDate: LocalDate
    fun toMap(): Map<HeaderFieldType, String>
}
```

### Метод `toMap()`

`toMap()` преобразует реквизиты в карту, где:
- **Ключ** — `HeaderFieldType` (тип поля, например «Номер», «Дата»).
- **Значение** — строковое представление значения.

Это нужно для подстановки значений в шаблон Word-документа. Каждый плейсхолдер в шаблоне (например, `{{number}}`) соответствует определённому `HeaderFieldType`.

---

## Шаг 2.4. Типы полей шапки

`HeaderFieldType` — это enum, который описывает все возможные поля шапки. Каждое поле имеет:
- `stringResource` — локализованное название (для UI).
- `placeholder` — плейсхолдер в шаблоне Word-документа.

**Файл:** `shared/src/commonMain/kotlin/.../domain/document/header/HeaderFieldType.kt`

```kotlin
package ru.example.docmanager.domain.document.header

import docmanager.shared.generated.resources.*
import org.jetbrains.compose.resources.StringResource

enum class HeaderFieldType(
    val stringResource: StringResource,
    val placeholder: String,
) {
    // --- ОБЩИЕ ПОЛЯ (есть у всех документов) ---
    NUMBER(Res.string.header_field_number, "{{number}}"),
    DISCHARGE_DATE(Res.string.header_field_discharge_date, "{{dischargeDate}}"),

    // --- ПОЛЯ ДОВЕРЕННОСТИ ---
    END_DATE(Res.string.header_field_end_date, "{{endDate}}"),
    SUPPLIER_AGREEMENT(Res.string.header_field_supplier_agreement, "{{supplierAgreement}}"),
    INDIVIDUAL_JOB(Res.string.header_field_individual_job, "{{individualJob}}"),
    INDIVIDUAL_NAME(Res.string.header_field_individual_name, "{{individualName}}"),
    INDIVIDUAL_SERIES(Res.string.header_field_individual_series, "{{individualSeries}}"),
    INDIVIDUAL_NUMBER(Res.string.header_field_individual_number, "{{individualNumber}}"),
    INDIVIDUAL_ISSUED(Res.string.header_field_individual_issued, "{{individualIssued}}"),
    INDIVIDUAL_DATE(Res.string.header_field_individual_date, "{{individualDate}}"),
    ORGANIZATION_NAME(Res.string.header_field_organization_name, "{{organizationName}}"),
    ORGANIZATION_CONSUMER(Res.string.header_field_organization_consumer, "{{organizationConsumer}}"),
    ORGANIZATION_PAYER(Res.string.header_field_organization_payer, "{{organizationPayer}}"),
    ORGANIZATION_ACCOUNT(Res.string.header_field_organization_account, "{{organizationAccount}}"),
    PRODUCT_NAME(Res.string.header_field_product_name, "{{productName}}"),
    SUPPLIER_NAME(Res.string.header_field_supplier_name, "{{supplierName}}"),
}
```

### Почему `placeholder` в enum?

- **Единый источник истины.** Плейсхолдер определён один раз, и если вы его измените, компилятор покажет все места, где он используется.
- **Связь с UI.** `stringResource` позволяет отображать название поля в форме редактирования.
- **Гибкость.** Общие поля (`NUMBER`, `DISCHARGE_DATE`) есть у всех документов, а специфические (`END_DATE`, `SUPPLIER_AGREEMENT`) — только у доверенности.

---

## Шаг 2.5. Тело документа

Не все документы имеют тело. Для документов с табличной частью предусмотрен интерфейс `WithBody`:

**Файл:** `shared/src/commonMain/kotlin/.../domain/document/WithBody.kt`

```kotlin
package ru.example.docmanager.domain.document

import ru.example.docmanager.domain.document.body.DocumentBody
import ru.example.docmanager.domain.document.body.DocumentBodyItem

interface WithBody<T : DocumentBodyItem> {
    val body: DocumentBody<T>
}
```

### Почему обобщённый тип `<T : DocumentBodyItem>`?

Разные типы документов могут иметь разные элементы тела. Доверенность имеет `PowerOfAttorneyBodyItem` (продукт, единица, количество), а другой тип документа может иметь совершенно другие поля. Обобщённый тип позволяет:
- Сохранить типобезопасность.
- Не смешивать элементы разных типов документов.

---

## Шаг 2.6. Интерфейсы тела

**Файл:** `shared/src/commonMain/kotlin/.../domain/document/body/DocumentBody.kt`

```kotlin
package ru.example.docmanager.domain.document.body

interface DocumentBody<T : DocumentBodyItem> {
    val items: List<T>
    fun toMapByColumns(): Map<BodyFieldType, List<String>>
}
```

**Файл:** `shared/src/commonMain/kotlin/.../domain/document/body/DocumentBodyItem.kt`

```kotlin
package ru.example.docmanager.domain.document.body

interface DocumentBodyItem {
    val id: Int
}
```

### Метод `toMapByColumns()`

Возвращает карту, где:
- **Ключ** — `BodyFieldType` (тип колонки: «Номер по порядку», «Материальные ценности» и т.д.).
- **Значение** — `List<String>` — список строк для каждой строки таблицы.

Это используется при экспорте в Word: `DocumentEngine` находит таблицу в шаблоне и заполняет её данными по колонкам.

---

## Шаг 2.7. Типы полей тела

**Файл:** `shared/src/commonMain/kotlin/.../domain/document/body/BodyFieldType.kt`

```kotlin
package ru.example.docmanager.domain.document.body

import docmanager.shared.generated.resources.*
import org.jetbrains.compose.resources.StringResource

enum class BodyFieldType(
    val stringResource: StringResource
) {
    NUMBER_SORTED(Res.string.body_field_number_sorted),
    PRODUCTS(Res.string.body_field_products),
    UNIT(Res.string.body_field_unit),
    COUNT(Res.string.body_field_count),
}
```

---

## Шаг 2.8. Справочники

Справочники (references) — это вспомогательные сущности, которые используются в документах: организации, физические лица, продукты, поставщики.

**Файл:** `shared/src/commonMain/kotlin/.../domain/reference/Reference.kt`

```kotlin
package ru.example.docmanager.domain.reference

import ru.example.docmanager.domain.document.header.HeaderFieldType

abstract class Reference(
    open var id: Int,
    val type: ReferenceType
) {
    abstract fun toMap(): Map<HeaderFieldType, String>
    abstract fun copyWithFields(fields: Map<HeaderFieldType, String>): Reference

    override fun equals(other: Any?): Boolean =
        other is Reference && this.id == other.id

    override fun hashCode(): Int = id
    override fun toString(): String = "Item(id=$id)"
}
```

### Почему абстрактный класс, а не интерфейс?

- **Общий код.** `equals()`, `hashCode()`, `toString()` реализованы один раз для всех справочников.
- **`id` и `type`** — общие свойства, которые есть у всех справочников.
- **`toMap()` и `copyWithFields()`** — абстрактные методы, которые каждый подкласс реализует по-своему.

### Метод `copyWithFields()`

Создаёт копию справочника с обновлёнными полями. Используется при редактировании: пользователь вводит данные в форму, и `copyWithFields()` создаёт новую версию справочника с этими данными.

---

## Шаг 2.9. Типы справочников

**Файл:** `shared/src/commonMain/kotlin/.../domain/reference/ReferenceType.kt`

```kotlin
package ru.example.docmanager.domain.reference

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Business
import androidx.compose.material.icons.rounded.Face
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.ShoppingCart
import androidx.compose.ui.graphics.vector.ImageVector
import docmanager.shared.generated.resources.*
import org.jetbrains.compose.resources.StringResource

enum class ReferenceType(
    val stringResource: StringResource,
    val icon: ImageVector
) {
    INDIVIDUAL(
        stringResource = Res.string.reference_individuals,
        icon = Icons.Rounded.Face
    ),
    ORGANIZATION(
        stringResource = Res.string.reference_organizations,
        icon = Icons.Rounded.Business
    ),
    PRODUCT(
        stringResource = Res.string.reference_products,
        icon = Icons.Rounded.ShoppingCart
    ),
    SUPPLIER(
        stringResource = Res.string.reference_suppliers,
        icon = Icons.Rounded.Person
    )
}
```
