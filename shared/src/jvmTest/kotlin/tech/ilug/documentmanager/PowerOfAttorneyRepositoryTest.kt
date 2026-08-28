package tech.ilug.documentmanager

import kotlinx.coroutines.runBlocking
import kotlinx.datetime.LocalDate
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.SchemaUtils
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import tech.ilug.documentmanager.database.ProjectMetadataTable
import tech.ilug.documentmanager.database.document.PowerOfAttorneyTables
import tech.ilug.documentmanager.model.PowerOfAttorney
import tech.ilug.documentmanager.repository.documents.PowerOfAttorneyRepository
import tech.ilug.documentmanager.repository.references.IndividualsRepository
import tech.ilug.documentmanager.repository.references.OrganizationsRepository
import tech.ilug.documentmanager.repository.references.ProductsRepository
import tech.ilug.documentmanager.repository.references.SuppliersRepository
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails

class PowerOfAttorneyRepositoryTest {

    private lateinit var individualsRepo: IndividualsRepository
    private lateinit var organizationsRepo: OrganizationsRepository
    private lateinit var productsRepo: ProductsRepository
    private lateinit var suppliersRepo: SuppliersRepository
    private lateinit var poaRepo: PowerOfAttorneyRepository

    @BeforeTest
    fun setUp() {
        Database.connect(
            "jdbc:h2:mem:test_db_${System.currentTimeMillis()};DB_CLOSE_DELAY=-1",
            driver = "org.h2.Driver"
        )
        transaction {
            SchemaUtils.create(
                ProjectMetadataTable,
                PowerOfAttorneyTables.ProductsTable,
                PowerOfAttorneyTables.SuppliersTable,
                PowerOfAttorneyTables.IndividualsTable,
                PowerOfAttorneyTables.OrganizationsTable,
                PowerOfAttorneyTables.HeadersTable,
                PowerOfAttorneyTables.BodiesTable
            )
        }

        individualsRepo = IndividualsRepository()
        organizationsRepo = OrganizationsRepository()
        productsRepo = ProductsRepository()
        suppliersRepo = SuppliersRepository()
        poaRepo = PowerOfAttorneyRepository(
            individualsRepository = individualsRepo,
            organizationsRepository = organizationsRepo,
            productsRepository = productsRepo,
            suppliersRepository = suppliersRepo
        )
    }

    @Test
    fun testCreateAndGetDocument() {
        runBlocking {
            val org = organizationsRepo.createItem(
                PowerOfAttorney.Organization(
                    name = "АО Компания",
                    consumer = "АО Потребитель",
                    payer = "АО Плательщик",
                    account = "1234567890"
                )
            )
            val ind = individualsRepo.createItem(
                PowerOfAttorney.Individual(
                    job = "Менеджер",
                    name = "Сидоров С.С.",
                    series = "4500",
                    number = "123456",
                    issued = "УВД",
                    date = LocalDate(2021, 5, 10)
                )
            )
            val sup = suppliersRepo.createItem(PowerOfAttorney.Supplier(name = "ООO Поставщик"))
            val prod1 = productsRepo.createItem(PowerOfAttorney.Product(name = "Монитор"))
            val prod2 = productsRepo.createItem(PowerOfAttorney.Product(name = "Клавиатура"))

            val header = PowerOfAttorney.Header(
                id = 1,
                organization = org,
                number = 42,
                dischargeDate = LocalDate(2026, 8, 28),
                endDate = LocalDate(2026, 9, 28),
                individual = ind,
                supplier = sup,
                supplierAgreement = "Договор купли-продажи"
            )
            val bodies = listOf(
                PowerOfAttorney.Body(
                    id = 0,
                    count = "5",
                    unit = "шт",
                    header = header,
                    product = prod1
                ),
                PowerOfAttorney.Body(
                    id = 0,
                    count = "10",
                    unit = "шт",
                    header = header,
                    product = prod2
                )
            )

            poaRepo.createDocument(PowerOfAttorney(header, bodies))

            val retrieved = poaRepo.getDocument(1)
            assertEquals(42, retrieved.header.number)
            assertEquals("АО Компания", retrieved.header.organization.name)
            assertEquals("АО Потребитель", retrieved.header.organization.consumer)
            assertEquals("Сидоров С.С.", retrieved.header.individual.name)
            assertEquals("Менеджер", retrieved.header.individual.job)
            assertEquals("ООO Поставщик", retrieved.header.supplier.name)
            assertEquals("Договор купли-продажи", retrieved.header.supplierAgreement)
            assertEquals(2, retrieved.body.size)
            assertEquals("Монитор", retrieved.body[0].product.name)
            assertEquals("5", retrieved.body[0].count)
            assertEquals("Клавиатура", retrieved.body[1].product.name)
            assertEquals("10", retrieved.body[1].count)
        }
    }

    @Test
    fun testUpdateDocument() {
        runBlocking {
            val org = organizationsRepo.createItem(
                PowerOfAttorney.Organization(name = "Орг1", consumer = "Потр1", payer = "Плат1", account = "111")
            )
            val ind = individualsRepo.createItem(
                PowerOfAttorney.Individual(job = "Инженер", name = "Петров П.П.", series = "11", number = "22", issued = "ОВД", date = LocalDate(2020, 1, 1))
            )
            val sup = suppliersRepo.createItem(PowerOfAttorney.Supplier(name = "Поставщик1"))
            val prod = productsRepo.createItem(PowerOfAttorney.Product(name = "Ноутбук"))

            val header = PowerOfAttorney.Header(
                id = 1,
                organization = org,
                number = 1,
                dischargeDate = LocalDate(2026, 8, 28),
                endDate = LocalDate(2026, 9, 28),
                individual = ind,
                supplier = sup,
                supplierAgreement = "Договор 1"
            )
            val bodies = listOf(
                PowerOfAttorney.Body(id = 0, count = "1", unit = "шт", header = header, product = prod)
            )
            poaRepo.createDocument(PowerOfAttorney(header, bodies))

            val updatedHeader = header.copy(number = 99, supplierAgreement = "Договор Обновленный")
            val prod2 = productsRepo.createItem(PowerOfAttorney.Product(name = "Мышь"))
            val updatedBodies = listOf(
                PowerOfAttorney.Body(id = 0, count = "20", unit = "шт", header = updatedHeader, product = prod2)
            )
            poaRepo.updateDocument(PowerOfAttorney(updatedHeader, updatedBodies))

            val retrieved = poaRepo.getDocument(1)
            assertEquals(99, retrieved.header.number)
            assertEquals("Договор Обновленный", retrieved.header.supplierAgreement)
            assertEquals(1, retrieved.body.size)
            assertEquals("Мышь", retrieved.body[0].product.name)
            assertEquals("20", retrieved.body[0].count)
        }
    }

    @Test
    fun testDeleteDocument() {
        runBlocking {
            val org = organizationsRepo.createItem(PowerOfAttorney.Organization(name = "Орг", consumer = "Потр", payer = "Плат", account = "111"))
            val ind = individualsRepo.createItem(PowerOfAttorney.Individual(job = "Должность", name = "ФИО", series = "1", number = "2", issued = "Кем", date = LocalDate(2020, 1, 1)))
            val sup = suppliersRepo.createItem(PowerOfAttorney.Supplier(name = "Поставщик"))
            val prod = productsRepo.createItem(PowerOfAttorney.Product(name = "Товар"))

            val header = PowerOfAttorney.Header(
                id = 1,
                organization = org,
                number = 5,
                dischargeDate = LocalDate(2026, 8, 28),
                endDate = LocalDate(2026, 9, 28),
                individual = ind,
                supplier = sup,
                supplierAgreement = "Дог"
            )
            poaRepo.createDocument(PowerOfAttorney(header, listOf(PowerOfAttorney.Body(id = 0, count = "1", unit = "шт", header = header, product = prod))))

            poaRepo.deleteDocument(1)

            assertFails {
                poaRepo.getDocument(1)
            }
        }
    }

    @Test
    fun testGetAllDocuments() {
        runBlocking {
            val org = organizationsRepo.createItem(PowerOfAttorney.Organization(name = "Орг", consumer = "Потр", payer = "Плат", account = "111"))
            val ind = individualsRepo.createItem(PowerOfAttorney.Individual(job = "Должность", name = "ФИО", series = "1", number = "2", issued = "Кем", date = LocalDate(2020, 1, 1)))
            val sup = suppliersRepo.createItem(PowerOfAttorney.Supplier(name = "Поставщик"))
            val prod1 = productsRepo.createItem(PowerOfAttorney.Product(name = "Товар 1"))
            val prod2 = productsRepo.createItem(PowerOfAttorney.Product(name = "Товар 2"))

            val header1 = PowerOfAttorney.Header(
                id = 0,
                organization = org,
                number = 101,
                dischargeDate = LocalDate(2026, 8, 28),
                endDate = LocalDate(2026, 9, 28),
                individual = ind,
                supplier = sup,
                supplierAgreement = "Дог 1"
            )
            val header2 = PowerOfAttorney.Header(
                id = 0,
                organization = org,
                number = 102,
                dischargeDate = LocalDate(2026, 8, 29),
                endDate = LocalDate(2026, 9, 29),
                individual = ind,
                supplier = sup,
                supplierAgreement = "Дог 2"
            )

            poaRepo.createDocument(PowerOfAttorney(header1, listOf(PowerOfAttorney.Body(id = 0, count = "1", unit = "шт", header = header1, product = prod1))))
            poaRepo.createDocument(PowerOfAttorney(header2, listOf(PowerOfAttorney.Body(id = 0, count = "2", unit = "кг", header = header2, product = prod2))))

            val all = poaRepo.getAllDocuments()
            assertEquals(2, all.size)
            assertEquals(101, all[0].header.number)
            assertEquals(102, all[1].header.number)
            assertEquals(1, all[0].body.size)
            assertEquals("Товар 1", all[0].body[0].product.name)
            assertEquals(1, all[1].body.size)
            assertEquals("Товар 2", all[1].body[0].product.name)
        }
    }
}
