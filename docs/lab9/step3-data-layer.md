# Шаг 3. Слой данных

Слой данных связывает доменные интерфейсы с реальным хранением — базой данных. Мы используем **JetBrains Exposed** — Kotlin-фреймворк для работы с SQL, который поддерживает KMP.

Слой данных состоит из:
1. **Моделей** — data class, которые реализуют доменные интерфейсы.
2. **Таблиц** — описание структуры БД через Exposed.
3. **Репозиториев** — CRUD-операции над данными.
4. **DatabaseManager** — подключение к БД и миграции.

---

## Шаг 3.1. Модель «Доверенность»

Доверенность — это документ с шапкой и телом. Она реализует интерфейсы `Document` и `WithBody`:

**Файл:** `shared/src/commonMain/kotlin/.../data/document/powerofattorney/PowerOfAttorney.kt`

```kotlin
package ru.example.docmanager.data.document.powerofattorney

import ru.example.docmanager.domain.document.Document
import ru.example.docmanager.domain.document.DocumentType
import ru.example.docmanager.domain.document.WithBody

data class PowerOfAttorney(
    override val header: PowerOfAttorneyHeader,
    override val body: PowerOfAttorneyBody,
    override val type: DocumentType = DocumentType.POWER_OF_ATTORNEY
) : Document, WithBody<PowerOfAttorneyBodyItem>
```

### Почему `data class`?

- **`copy()`** — автоматически генерируется, нужен для создания изменённой копии документа.
- **`equals()`/`hashCode()`** — нужны для сравнения документов.
- **`toString()`** — удобен при отладке.

---

### Шапка доверенности

Шапка содержит все реквизиты: номер, даты, организацию, физическое лицо, поставщика и номер договора.

**Файл:** `shared/src/commonMain/kotlin/.../data/document/powerofattorney/PowerOfAttorneyHeader.kt`

```kotlin
package ru.example.docmanager.data.document.powerofattorney

import kotlinx.datetime.LocalDate
import ru.example.docmanager.data.reference.Individual
import ru.example.docmanager.data.reference.Organization
import ru.example.docmanager.data.reference.Supplier
import ru.example.docmanager.domain.document.header.DocumentHeader
import ru.example.docmanager.domain.document.header.HeaderFieldType
import ru.example.docmanager.ui.utils.toRussianString

data class PowerOfAttorneyHeader(
    override val id: Int,
    override var number: Int,
    override var dischargeDate: LocalDate,
    var organization: Organization,
    var endDate: LocalDate,
    var individual: Individual,
    var supplier: Supplier,
    var supplierAgreement: String
) : DocumentHeader {
    override fun toMap(): Map<HeaderFieldType, String> = mapOf(
        HeaderFieldType.NUMBER to number.toString(),
        HeaderFieldType.DISCHARGE_DATE to dischargeDate.toRussianString(),
        HeaderFieldType.END_DATE to endDate.toRussianString(),
        HeaderFieldType.SUPPLIER_AGREEMENT to supplierAgreement,
    ) + organization.toMap() +
            individual.toMap() +
            supplier.toMap()
}
```

### Как работает `toMap()`?

Метод объединяет карты всех справочников оператором `+`:
1. Создаёт базовую карту с номером, датами и договором.
2. Добавляет карту организации (`organization.toMap()`).
3. Добавляет карту физического лица (`individual.toMap()`).
4. Добавляет карту поставщика (`supplier.toMap()`).

Результат — одна большая карта, где каждый `HeaderFieldType` отображается в строковое значение. Эта карта используется при экспорте в Word.

---

### Тело доверенности

Тело — это список позиций (продукт, единица измерения, количество):

**Файл:** `shared/src/commonMain/kotlin/.../data/document/powerofattorney/PowerOfAttorneyBody.kt`

```kotlin
package ru.example.docmanager.data.document.powerofattorney

import ru.example.docmanager.domain.document.body.BodyFieldType
import ru.example.docmanager.domain.document.body.DocumentBody

data class PowerOfAttorneyBody(
    override val items: List<PowerOfAttorneyBodyItem>
) : DocumentBody<PowerOfAttorneyBodyItem> {
    override fun toMapByColumns() = mapOf(
        BodyFieldType.NUMBER_SORTED to List(items.size) { idx -> (idx + 1).toString() },
        BodyFieldType.PRODUCTS to items.map { it.product.name },
        BodyFieldType.UNIT to items.map { it.unit },
        BodyFieldType.COUNT to items.map { it.count }
    )
}
```

### Элемент тела

**Файл:** `shared/src/commonMain/kotlin/.../data/document/powerofattorney/PowerOfAttorneyBodyItem.kt`

```kotlin
package ru.example.docmanager.data.document.powerofattorney

import ru.example.docmanager.data.reference.Product
import ru.example.docmanager.domain.document.body.DocumentBodyItem

data class PowerOfAttorneyBodyItem(
    override val id: Int,
    var count: String = "",
    var unit: String = "",
    val product: Product
) : DocumentBodyItem
```

---

## Шаг 3.2. Модели справочников

Каждый справочник наследует `Reference` и реализует `toMap()` и `copyWithFields()`.

### Организация

**Файл:** `shared/src/commonMain/kotlin/.../data/reference/Organization.kt`

```kotlin
package ru.example.docmanager.data.reference

import ru.example.docmanager.domain.document.header.HeaderFieldType
import ru.example.docmanager.domain.reference.Reference
import ru.example.docmanager.domain.reference.ReferenceType

data class Organization(
    override var id: Int = -1,
    val name: String = "",
    val consumer: String = "",
    val payer: String = "",
    val account: String = ""
) : Reference(id, ReferenceType.ORGANIZATION) {
    override fun toMap() = mapOf(
        HeaderFieldType.ORGANIZATION_NAME to name,
        HeaderFieldType.ORGANIZATION_CONSUMER to consumer,
        HeaderFieldType.ORGANIZATION_PAYER to payer,
        HeaderFieldType.ORGANIZATION_ACCOUNT to account,
    )

    override fun copyWithFields(fields: Map<HeaderFieldType, String>) = copy(
        name = (fields[HeaderFieldType.ORGANIZATION_NAME] ?: name),
        consumer = (fields[HeaderFieldType.ORGANIZATION_CONSUMER] ?: consumer),
        payer = (fields[HeaderFieldType.ORGANIZATION_PAYER] ?: payer),
        account = (fields[HeaderFieldType.ORGANIZATION_ACCOUNT] ?: account)
    )
}
```

### Физическое лицо

**Файл:** `shared/src/commonMain/kotlin/.../data/reference/Individual.kt`

```kotlin
package ru.example.docmanager.data.reference

import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import ru.example.docmanager.domain.document.header.HeaderFieldType
import ru.example.docmanager.domain.reference.Reference
import ru.example.docmanager.domain.reference.ReferenceType
import ru.example.docmanager.ui.utils.parseRussianOrNull
import ru.example.docmanager.ui.utils.toRussianString
import kotlin.time.Clock

data class Individual(
    override var id: Int = -1,
    val job: String = "",
    val name: String = "",
    val series: String = "",
    val number: String = "",
    val issued: String = "",
    val date: LocalDate = Clock.System.todayIn(TimeZone.currentSystemDefault())
) : Reference(id, ReferenceType.INDIVIDUAL) {
    override fun toMap() = mapOf(
        HeaderFieldType.INDIVIDUAL_JOB to job,
        HeaderFieldType.INDIVIDUAL_NAME to name,
        HeaderFieldType.INDIVIDUAL_SERIES to series,
        HeaderFieldType.INDIVIDUAL_NUMBER to number,
        HeaderFieldType.INDIVIDUAL_ISSUED to issued,
        HeaderFieldType.INDIVIDUAL_DATE to date.toRussianString(),
    )

    override fun copyWithFields(fields: Map<HeaderFieldType, String>) = copy(
        job = (fields[HeaderFieldType.INDIVIDUAL_JOB] ?: job),
        name = (fields[HeaderFieldType.INDIVIDUAL_NAME] ?: name),
        series = (fields[HeaderFieldType.INDIVIDUAL_SERIES] ?: series),
        number = (fields[HeaderFieldType.INDIVIDUAL_NUMBER] ?: number),
        issued = (fields[HeaderFieldType.INDIVIDUAL_ISSUED] ?: issued),
        date = (fields[HeaderFieldType.INDIVIDUAL_DATE] ?: "").parseRussianOrNull() ?: date
    )
}
```

### Продукт

**Файл:** `shared/src/commonMain/kotlin/.../data/reference/Product.kt`

```kotlin
package ru.example.docmanager.data.reference

import ru.example.docmanager.domain.document.header.HeaderFieldType
import ru.example.docmanager.domain.reference.Reference
import ru.example.docmanager.domain.reference.ReferenceType

data class Product(
    override var id: Int = -1,
    val name: String = ""
) : Reference(id, ReferenceType.PRODUCT) {
    override fun toMap() = mapOf(
        HeaderFieldType.PRODUCT_NAME to name,
    )

    override fun copyWithFields(fields: Map<HeaderFieldType, String>) = copy(
        name = (fields[HeaderFieldType.PRODUCT_NAME] ?: name)
    )
}
```

### Поставщик

**Файл:** `shared/src/commonMain/kotlin/.../data/reference/Supplier.kt`

```kotlin
package ru.example.docmanager.data.reference

import ru.example.docmanager.domain.document.header.HeaderFieldType
import ru.example.docmanager.domain.reference.Reference
import ru.example.docmanager.domain.reference.ReferenceType

data class Supplier(
    override var id: Int = -1,
    val name: String = ""
) : Reference(id, ReferenceType.SUPPLIER) {
    override fun toMap() = mapOf(
        HeaderFieldType.SUPPLIER_NAME to name
    )

    override fun copyWithFields(fields: Map<HeaderFieldType, String>) = copy(
        name = (fields[HeaderFieldType.SUPPLIER_NAME] ?: name)
    )
}
```

> [!NOTE]
> **Почему `id = -1` по умолчанию?** Значение `-1` означает «новый объект, ещё не сохранённый в БД». При сохранении репозиторий заменяет `-1` на реальный ID, выданный базой данных.

---

## Шаг 3.3. Таблицы базы данных (Exposed)

Каждая таблица наследует `IntIdTable` — это готовый класс Exposed с автоинкрементным `id`.

### Справочники

**Файл:** `shared/src/commonMain/kotlin/.../data/database/tables/references/OrganizationsTable.kt`

```kotlin
package ru.example.docmanager.data.database.tables.references

import org.jetbrains.exposed.v1.core.dao.id.IntIdTable

object OrganizationsTable : IntIdTable("organizations") {
    val name = varchar("name", 256)
    val consumer = varchar("consumer", 256)
    val payer = varchar("payer", 256)
    val account = varchar("account", 256)
}
```

**Файл:** `shared/src/commonMain/kotlin/.../data/database/tables/references/IndividualsTable.kt`

```kotlin
package ru.example.docmanager.data.database.tables.references

import org.jetbrains.exposed.v1.core.dao.id.IntIdTable
import org.jetbrains.exposed.v1.datetime.date

object IndividualsTable : IntIdTable("individuals") {
    val job = varchar("job", 128)
    val name = varchar("name", 128)
    val series = varchar("series", 16)
    val number = varchar("number", 16)
    val issued = varchar("issued", 128)
    val date = date("date")
}
```

**Файл:** `shared/src/commonMain/kotlin/.../data/database/tables/references/ProductsTable.kt`

```kotlin
package ru.example.docmanager.data.database.tables.references

import org.jetbrains.exposed.v1.core.dao.id.IntIdTable

object ProductsTable : IntIdTable("products") {
    val name = varchar("name", 256)
}
```

**Файл:** `shared/src/commonMain/kotlin/.../data/database/tables/references/SuppliersTable.kt`

```kotlin
package ru.example.docmanager.data.database.tables.references

import org.jetbrains.exposed.v1.core.dao.id.IntIdTable

object SuppliersTable : IntIdTable("suppliers") {
    val name = varchar("name", 256)
}
```

### Шапка доверенности

**Файл:** `shared/src/commonMain/kotlin/.../data/database/tables/documents/powerofattorney/PowerOfAttorneyHeadersTable.kt`

```kotlin
package ru.example.docmanager.data.database.tables.documents.powerofattorney

import org.jetbrains.exposed.v1.core.ReferenceOption
import org.jetbrains.exposed.v1.core.dao.id.IntIdTable
import org.jetbrains.exposed.v1.datetime.date
import ru.example.docmanager.data.database.tables.references.IndividualsTable
import ru.example.docmanager.data.database.tables.references.OrganizationsTable
import ru.example.docmanager.data.database.tables.references.SuppliersTable

object PowerOfAttorneyHeadersTable : IntIdTable("powerOfAttorneyHeaders") {
    val organizationId = integer("organizationId")
        .references(OrganizationsTable.id, onDelete = ReferenceOption.CASCADE)
    val number = integer("number")
    val dischargeDate = date("dischargeDate")
    val endDate = date("endDate")
    val individualId = integer("individualId")
        .references(IndividualsTable.id, onDelete = ReferenceOption.CASCADE)
    val supplierId = integer("supplierId")
        .references(SuppliersTable.id, onDelete = ReferenceOption.CASCADE)
    val supplierAgreement = varchar("supplierAgreement", 256)
}
```

### Тело доверенности

**Файл:** `shared/src/commonMain/kotlin/.../data/database/tables/documents/powerofattorney/PowerOfAttorneyBodiesTable.kt`

```kotlin
package ru.example.docmanager.data.database.tables.documents.powerofattorney

import org.jetbrains.exposed.v1.core.ReferenceOption
import org.jetbrains.exposed.v1.core.dao.id.IntIdTable
import ru.example.docmanager.data.database.tables.references.ProductsTable

object PowerOfAttorneyBodiesTable : IntIdTable("powerOfAttorneyBodies") {
    val count = varchar("count", 64).default("одна")
    val unit = varchar("unit", 256)
    val headerId = integer("headerId")
        .references(PowerOfAttorneyHeadersTable.id, onDelete = ReferenceOption.CASCADE)
    val productId = integer("productId")
        .references(ProductsTable.id, onDelete = ReferenceOption.CASCADE)
}
```

### Почему `.references(..., onDelete = ReferenceOption.CASCADE)`?

Это внешний ключ (foreign key) с каскадным удалением. Если вы удалите организацию из справочника, все доверенности, которые ссылаются на эту организацию, будут автоматически удалены. Это предотвращает «висячие» ссылки.

---

## Шаг 3.4. Репозитории

### Базовый интерфейс репозитория документов

**Файл:** `shared/src/commonMain/kotlin/.../data/repositories/documents/BaseDocumentRepository.kt`

```kotlin
package ru.example.docmanager.data.repositories.documents

import ru.example.docmanager.domain.document.DocumentType

interface BaseDocumentRepository {
    val type: DocumentType
    suspend fun deleteDocument(id: Int)
}
```

**Файл:** `shared/src/commonMain/kotlin/.../data/repositories/documents/DocumentRepository.kt`

```kotlin
package ru.example.docmanager.data.repositories.documents

import ru.example.docmanager.domain.document.Document

interface DocumentRepository<T : Document> : BaseDocumentRepository {
    suspend fun createDocument(document: T): T
    suspend fun updateDocument(document: T)
    suspend fun getDocument(id: Int): T
    suspend fun getAllDocuments(): List<T>
}
```

### Репозиторий доверенностей

**Файл:** `shared/src/commonMain/kotlin/.../data/repositories/documents/PowerOfAttorneyRepository.kt`

```kotlin
package ru.example.docmanager.data.repositories.documents

import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.*
import ru.example.docmanager.data.database.dbQuery
import ru.example.docmanager.data.database.tables.documents.powerofattorney.PowerOfAttorneyBodiesTable
import ru.example.docmanager.data.database.tables.documents.powerofattorney.PowerOfAttorneyHeadersTable
import ru.example.docmanager.data.database.tables.references.IndividualsTable
import ru.example.docmanager.data.database.tables.references.OrganizationsTable
import ru.example.docmanager.data.database.tables.references.ProductsTable
import ru.example.docmanager.data.database.tables.references.SuppliersTable
import ru.example.docmanager.data.document.powerofattorney.*
import ru.example.docmanager.data.reference.*
import ru.example.docmanager.domain.document.DocumentType

class PowerOfAttorneyRepository : DocumentRepository<PowerOfAttorney> {
    override val type = DocumentType.POWER_OF_ATTORNEY

    override suspend fun createDocument(document: PowerOfAttorney) = dbQuery {
        // 1. Сохраняем шапку
        val headerId = PowerOfAttorneyHeadersTable.insertAndGetId {
            it[organizationId] = document.header.organization.id
            it[number] = document.header.number
            it[dischargeDate] = document.header.dischargeDate
            it[endDate] = document.header.endDate
            it[individualId] = document.header.individual.id
            it[supplierId] = document.header.supplier.id
            it[supplierAgreement] = document.header.supplierAgreement
        }.value

        // 2. Сохраняем каждую позицию тела
        document.body.items.forEach { body ->
            PowerOfAttorneyBodiesTable.insert {
                it[count] = body.count
                it[unit] = body.unit
                it[PowerOfAttorneyBodiesTable.headerId] = headerId
                it[productId] = body.product.id
            }
        }

        // 3. Возвращаем документ с реальным ID
        document.copy(header = document.header.copy(id = headerId))
    }

    override suspend fun updateDocument(document: PowerOfAttorney): Unit =
        dbQuery {
            // Обновляем шапку
            PowerOfAttorneyHeadersTable.update({
                PowerOfAttorneyHeadersTable.id eq document.header.id
            }) {
                it[organizationId] = document.header.organization.id
                it[number] = document.header.number
                it[dischargeDate] = document.header.dischargeDate
                it[endDate] = document.header.endDate
                it[individualId] = document.header.individual.id
                it[supplierId] = document.header.supplier.id
                it[supplierAgreement] = document.header.supplierAgreement
            }
            // Удаляем старые позиции и вставляем новые
            PowerOfAttorneyBodiesTable.deleteWhere {
                PowerOfAttorneyBodiesTable.headerId eq document.header.id
            }
            document.body.items.forEach { body ->
                PowerOfAttorneyBodiesTable.insert {
                    it[count] = body.count
                    it[unit] = body.unit
                    it[PowerOfAttorneyBodiesTable.headerId] = document.header.id
                    it[productId] = body.product.id
                }
            }
        }

    override suspend fun deleteDocument(id: Int): Unit = dbQuery {
        PowerOfAttorneyBodiesTable.deleteWhere {
            PowerOfAttorneyBodiesTable.headerId eq id
        }
        PowerOfAttorneyHeadersTable.deleteWhere {
            PowerOfAttorneyHeadersTable.id eq id
        }
    }

    override suspend fun getAllDocuments(): List<PowerOfAttorney> = dbQuery {
        val headerRows = PowerOfAttorneyHeadersTable.selectAll().toList()
        if (headerRows.isEmpty()) return@dbQuery emptyList()

        // Группируем позиции по headerId
        val bodyRows = PowerOfAttorneyBodiesTable.selectAll().toList().groupBy {
            it[PowerOfAttorneyBodiesTable.headerId]
        }

        // Загружаем все справочники в карты для быстрого поиска
        val orgMap = OrganizationsTable.selectAll().associate {
            it[OrganizationsTable.id].value to Organization(
                id = it[OrganizationsTable.id].value,
                name = it[OrganizationsTable.name],
                consumer = it[OrganizationsTable.consumer],
                payer = it[OrganizationsTable.payer],
                account = it[OrganizationsTable.account]
            )
        }
        val indMap = IndividualsTable.selectAll().associate {
            it[IndividualsTable.id].value to Individual(
                id = it[IndividualsTable.id].value,
                job = it[IndividualsTable.job],
                name = it[IndividualsTable.name],
                series = it[IndividualsTable.series],
                number = it[IndividualsTable.number],
                issued = it[IndividualsTable.issued],
                date = it[IndividualsTable.date]
            )
        }
        val supMap = SuppliersTable.selectAll().associate {
            it[SuppliersTable.id].value to Supplier(
                id = it[SuppliersTable.id].value,
                name = it[SuppliersTable.name]
            )
        }
        val prodMap = ProductsTable.selectAll().associate {
            it[ProductsTable.id].value to Product(
                id = it[ProductsTable.id].value,
                name = it[ProductsTable.name]
            )
        }

        // Собираем документы
        headerRows.map { headerRow ->
            val id = headerRow[PowerOfAttorneyHeadersTable.id].value
            val header = PowerOfAttorneyHeader(
                id = id,
                organization = orgMap[headerRow[PowerOfAttorneyHeadersTable.organizationId]]
                    ?: Organization(),
                number = headerRow[PowerOfAttorneyHeadersTable.number],
                dischargeDate = headerRow[PowerOfAttorneyHeadersTable.dischargeDate],
                endDate = headerRow[PowerOfAttorneyHeadersTable.endDate],
                individual = indMap[headerRow[PowerOfAttorneyHeadersTable.individualId]]
                    ?: Individual(),
                supplier = supMap[headerRow[PowerOfAttorneyHeadersTable.supplierId]]
                    ?: Supplier(),
                supplierAgreement = headerRow[PowerOfAttorneyHeadersTable.supplierAgreement]
            )
            val docBodies = bodyRows[id].orEmpty().map { bRow ->
                PowerOfAttorneyBodyItem(
                    id = bRow[PowerOfAttorneyBodiesTable.id].value,
                    count = bRow[PowerOfAttorneyBodiesTable.count],
                    unit = bRow[PowerOfAttorneyBodiesTable.unit],
                    product = prodMap[bRow[PowerOfAttorneyBodiesTable.productId]]
                        ?: Product()
                )
            }
            PowerOfAttorney(header = header, body = PowerOfAttorneyBody(docBodies))
        }
    }

    override suspend fun getDocument(id: Int): PowerOfAttorney =
        dbQuery {
            val headerRow = PowerOfAttorneyHeadersTable.selectAll().where {
                PowerOfAttorneyHeadersTable.id eq id
            }.first()
            val bodyRows = PowerOfAttorneyBodiesTable.selectAll().where {
                PowerOfAttorneyBodiesTable.headerId eq id
            }.toList()

            // Загружаем справочники
            val orgRow = OrganizationsTable.selectAll().where {
                OrganizationsTable.id eq headerRow[PowerOfAttorneyHeadersTable.organizationId]
            }.firstOrNull()
            val indRow = IndividualsTable.selectAll().where {
                IndividualsTable.id eq headerRow[PowerOfAttorneyHeadersTable.individualId]
            }.firstOrNull()
            val supRow = SuppliersTable.selectAll().where {
                SuppliersTable.id eq headerRow[PowerOfAttorneyHeadersTable.supplierId]
            }.firstOrNull()

            val productIds = bodyRows.map { it[PowerOfAttorneyBodiesTable.productId] }.distinct()
            val prodMap = if (productIds.isNotEmpty()) {
                ProductsTable.selectAll().where {
                    ProductsTable.id inList productIds
                }.associate {
                    it[ProductsTable.id].value to Product(
                        id = it[ProductsTable.id].value,
                        name = it[ProductsTable.name]
                    )
                }
            } else emptyMap()

            val organization = orgRow?.let {
                Organization(it[OrganizationsTable.id].value, it[OrganizationsTable.name],
                    it[OrganizationsTable.consumer], it[OrganizationsTable.payer], it[OrganizationsTable.account])
            } ?: Organization()
            val individual = indRow?.let {
                Individual(it[IndividualsTable.id].value, it[IndividualsTable.job],
                    it[IndividualsTable.name], it[IndividualsTable.series],
                    it[IndividualsTable.number], it[IndividualsTable.issued], it[IndividualsTable.date])
            } ?: Individual()
            val supplier = supRow?.let {
                Supplier(it[SuppliersTable.id].value, it[SuppliersTable.name])
            } ?: Supplier()

            val header = PowerOfAttorneyHeader(
                id = id, organization = organization,
                number = headerRow[PowerOfAttorneyHeadersTable.number],
                dischargeDate = headerRow[PowerOfAttorneyHeadersTable.dischargeDate],
                endDate = headerRow[PowerOfAttorneyHeadersTable.endDate],
                individual = individual, supplier = supplier,
                supplierAgreement = headerRow[PowerOfAttorneyHeadersTable.supplierAgreement]
            )
            val bodyItemList = bodyRows.map { row ->
                PowerOfAttorneyBodyItem(
                    id = row[PowerOfAttorneyBodiesTable.id].value,
                    count = row[PowerOfAttorneyBodiesTable.count],
                    unit = row[PowerOfAttorneyBodiesTable.unit],
                    product = prodMap[row[PowerOfAttorneyBodiesTable.productId]] ?: Product()
                )
            }
            PowerOfAttorney(header = header, body = PowerOfAttorneyBody(bodyItemList))
        }
}
```

> [!IMPORTANT]
> **Почему `dbQuery { }`?** Это обёртка над `transaction { }` из Exposed, которая запускает запрос в IO-диспатчере корутин. Без этого БД-запросы блокировали бы UI-поток.

---

## Шаг 3.5. Репозитории справочников

**Файл:** `shared/src/commonMain/kotlin/.../data/repositories/references/BaseReferenceRepository.kt`

```kotlin
package ru.example.docmanager.data.repositories.references

import ru.example.docmanager.domain.reference.ReferenceType

interface BaseReferenceRepository {
    val type: ReferenceType
}
```

**Файл:** `shared/src/commonMain/kotlin/.../data/repositories/references/ReferenceRepository.kt`

```kotlin
package ru.example.docmanager.data.repositories.references

import ru.example.docmanager.domain.reference.Reference

abstract class ReferenceRepository<T : Reference>(
    val name: String
) : BaseReferenceRepository {
    abstract suspend fun getItem(id: Int): T
    abstract suspend fun getItems(): List<T>
    abstract suspend fun createItem(item: T): T
    abstract suspend fun createItem(): T
    abstract suspend fun updateItem(item: T)
    abstract suspend fun deleteItem(id: Int)
}
```

### Репозиторий организаций (пример)

**Файл:** `shared/src/commonMain/kotlin/.../data/repositories/references/OrganizationsRepository.kt`

```kotlin
package ru.example.docmanager.data.repositories.references

import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.*
import ru.example.docmanager.data.database.dbQuery
import ru.example.docmanager.data.database.tables.references.OrganizationsTable
import ru.example.docmanager.data.reference.Organization
import ru.example.docmanager.domain.reference.ReferenceType

class OrganizationsRepository : ReferenceRepository<Organization>("organizations") {
    override val type = ReferenceType.ORGANIZATION

    private fun ResultRow.toOrganization() = Organization(
        id = this[OrganizationsTable.id].value,
        name = this[OrganizationsTable.name],
        consumer = this[OrganizationsTable.consumer],
        payer = this[OrganizationsTable.payer],
        account = this[OrganizationsTable.account]
    )

    override suspend fun getItem(id: Int): Organization = dbQuery {
        OrganizationsTable.selectAll().where { OrganizationsTable.id eq id }
            .first().toOrganization()
    }

    override suspend fun getItems(): List<Organization> = dbQuery {
        OrganizationsTable.selectAll().map { it.toOrganization() }
    }

    override suspend fun createItem(): Organization = createItem(Organization())

    override suspend fun createItem(item: Organization): Organization = dbQuery {
        val newId = OrganizationsTable.insertAndGetId {
            it[name] = item.name
            it[consumer] = item.consumer
            it[payer] = item.payer
            it[account] = item.account
        }.value
        item.id = newId
        item
    }

    override suspend fun updateItem(item: Organization): Unit = dbQuery {
        OrganizationsTable.update({ OrganizationsTable.id eq item.id }) {
            it[name] = item.name
            it[consumer] = item.consumer
            it[payer] = item.payer
            it[account] = item.account
        }
    }

    override suspend fun deleteItem(id: Int): Unit = dbQuery {
        OrganizationsTable.deleteWhere { OrganizationsTable.id eq id }
    }
}
```

Аналогично реализованы `IndividualsRepository`, `ProductsRepository` и `SuppliersRepository`.

---

## Шаг 3.6. Управление базой данных

### Интерфейс

**Файл:** `shared/src/commonMain/kotlin/.../data/database/DatabaseManager.kt`

```kotlin
package ru.example.docmanager.data.database

interface DatabaseManager {
    fun init(jdbcUrl: String, driver: String, user: String?, password: String?)
    fun disconnect()
    fun isInitialized(): Boolean
}
```

### Реализация

**Файл:** `shared/src/commonMain/kotlin/.../data/database/DefaultDatabaseManager.kt`

```kotlin
package ru.example.docmanager.data.database

import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import org.jetbrains.exposed.v1.jdbc.*
import org.jetbrains.exposed.v1.jdbc.transactions.TransactionManager
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.jetbrains.exposed.v1.migration.jdbc.MigrationUtils
import ru.example.docmanager.data.database.tables.MetadataTable
import ru.example.docmanager.data.database.tables.documents.powerofattorney.PowerOfAttorneyBodiesTable
import ru.example.docmanager.data.database.tables.documents.powerofattorney.PowerOfAttorneyHeadersTable
import ru.example.docmanager.data.database.tables.references.IndividualsTable
import ru.example.docmanager.data.database.tables.references.OrganizationsTable
import ru.example.docmanager.data.database.tables.references.ProductsTable
import ru.example.docmanager.data.database.tables.references.SuppliersTable
import kotlin.time.Clock

class DefaultDatabaseManager : DatabaseManager {
    private var db: Database? = null
    private var initialized = false

    override fun init(jdbcUrl: String, driver: String, user: String?, password: String?) {
        if (this.initialized)
            throw IllegalStateException("Database already initialized")

        val database = Database.connect(
            url = jdbcUrl,
            driver = driver,
            user = user ?: "",
            password = password ?: ""
        )
        this.db = database
        TransactionManager.defaultDatabase = database

        transaction(database) {
            // Автоматическая миграция: Exposed сравнивает текущую схему с таблицами
            // и создаёт/изменяет таблицы при необходимости
            val statements = MigrationUtils.statementsRequiredForDatabaseMigration(
                MetadataTable,
                ProductsTable,
                SuppliersTable,
                IndividualsTable,
                OrganizationsTable,
                PowerOfAttorneyBodiesTable,
                PowerOfAttorneyHeadersTable
            )
            if (statements.isNotEmpty()) {
                execInBatch(statements)
            }

            // Если таблица метаданных пуста — создаём начальную запись
            if (MetadataTable.selectAll().empty()) {
                MetadataTable.insert {
                    it[name] = "Новый проект"
                    it[version] = "1.0"
                    it[creationDate] = Clock.System.todayIn(TimeZone.currentSystemDefault())
                    it[author] = "Пользователь"
                }
            }
        }

        this.initialized = true
    }

    override fun disconnect() {
        this.db?.let { TransactionManager.closeAndUnregister(it) }
        if (TransactionManager.defaultDatabase == this.db) {
            TransactionManager.defaultDatabase = null
        }
        this.db = null
        this.initialized = false
    }

    override fun isInitialized(): Boolean = this.initialized
}
```

### Утилита для корутин

**Файл:** `shared/src/commonMain/kotlin/.../data/database/DatabaseUtils.kt`

```kotlin
package ru.example.docmanager.data.database

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.exposed.v1.core.Transaction
import org.jetbrains.exposed.v1.jdbc.transactions.transaction

suspend fun <T> dbQuery(block: Transaction.() -> T): T = withContext(Dispatchers.IO) {
    transaction { block() }
}
```

> [!NOTE]
> **Почему `withContext(Dispatchers.IO)`?** БД-запросы — это медленные операции ввода-вывода. Запуск их в IO-диспатчере освобождает UI-поток, чтобы интерфейс оставался отзывчивым.

---

## Шаг 3.7. Драйверы и конфигурация подключения

**Файл:** `shared/src/commonMain/kotlin/.../data/database/DatabaseDriver.kt`

```kotlin
package ru.example.docmanager.data.database

enum class DatabaseDriver(
    val displayName: String,
    val driverClassName: String
) {
    H2(
        displayName = "H2 Database",
        driverClassName = "org.h2.Driver"
    ),
    POSTGRESQL(
        displayName = "PostgreSQL",
        driverClassName = "org.postgresql.Driver"
    );

    companion object {
        fun fromDriverClassName(driverClassName: String): DatabaseDriver? {
            return entries.firstOrNull {
                it.driverClassName.equals(driverClassName, ignoreCase = true)
            }
        }
    }
}
```

### expect/actual для URL по умолчанию

**Файл:** `shared/src/commonMain/kotlin/.../data/database/DefaultConnectionConfig.kt`

```kotlin
package ru.example.docmanager.data.database

expect val defaultH2JdbcUrl: String
expect val defaultPostgresJdbcUrl: String
```

**Файл:** `shared/src/jvmMain/kotlin/.../data/database/DefaultConnectionConfig.jvm.kt`

```kotlin
package ru.example.docmanager.data.database

actual val defaultH2JdbcUrl = "jdbc:h2:~/ru.example.docmanager/new_project;MODE=MySQL"
actual val defaultPostgresJdbcUrl = "jdbc:postgresql://localhost:5432/new_project"
```

**Файл:** `shared/src/androidMain/kotlin/.../data/database/DefaultConnectionConfig.android.kt`

```kotlin
package ru.example.docmanager.data.database

actual val defaultH2JdbcUrl = "jdbc:h2:/data/data/ru.example.docmanager/new_project;MODE=MySQL"
actual val defaultPostgresJdbcUrl = "jdbc:postgresql://localhost:5432/new_project"
```

> [!IMPORTANT]
> **Почему `expect/actual`?** Путь к H2-базе данных различается на Desktop (`~/`) и Android (`/data/data/...`). Механизм `expect/actual` позволяет определить общий интерфейс в `commonMain` и платформенную реализацию в `jvmMain`/`androidMain`.

---

**Следующий шаг:** [Шаг 4. Внедрение зависимостей](step4-di-layer.md) — связываем все слои с помощью Koin.
