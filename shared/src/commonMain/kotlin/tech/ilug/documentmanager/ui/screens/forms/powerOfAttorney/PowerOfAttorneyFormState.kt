package tech.ilug.documentmanager.ui.screens.forms.powerOfAttorney

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.datetime.LocalDate
import tech.ilug.documentmanager.model.PowerOfAttorney
import kotlin.collections.plus

class PowerOfAttorneyFormState(
    val today: LocalDate,
    val defaultEndDate: LocalDate
) {
    var existingDocuments by mutableStateOf<List<PowerOfAttorney>>(emptyList())
    var selectedDocumentId by mutableStateOf<Int?>(null)
    var numberDropdownExpanded by mutableStateOf(false)

    var number by mutableStateOf("1")
    var dischargeDate by mutableStateOf(DATE_FORMAT.format(today))
    var endDate by mutableStateOf(DATE_FORMAT.format(defaultEndDate))
    var selectedOrganization by mutableStateOf<PowerOfAttorney.Organization?>(null)
    var selectedIndividual by mutableStateOf<PowerOfAttorney.Individual?>(null)
    var selectedSupplier by mutableStateOf<PowerOfAttorney.Supplier?>(null)
    var supplierAgreement by mutableStateOf("")

    var bodyItems by mutableStateOf(listOf(PowerOfAttorneyBodyItem()))
    var statusMessage by mutableStateOf<String?>(null)

    val isEditing: Boolean get() = selectedDocumentId != null

    fun resetForm(
        organizations: List<PowerOfAttorney.Organization> = emptyList(),
        individuals: List<PowerOfAttorney.Individual> = emptyList(),
        suppliers: List<PowerOfAttorney.Supplier> = emptyList(),
        products: List<PowerOfAttorney.Product> = emptyList()
    ) {
        selectedDocumentId = null
        val nextNumber = if (existingDocuments.isNotEmpty()) {
            (existingDocuments.maxOfOrNull { it.header.number } ?: 0) + 1
        } else 1
        number = nextNumber.toString()
        dischargeDate = DATE_FORMAT.format(today)
        endDate = DATE_FORMAT.format(defaultEndDate)
        selectedOrganization = organizations.firstOrNull()
        selectedIndividual = individuals.firstOrNull()
        selectedSupplier = suppliers.firstOrNull()
        supplierAgreement = ""
        bodyItems = listOf(PowerOfAttorneyBodyItem(product = products.firstOrNull()))
        statusMessage = null
    }

    fun populateFromDocument(
        doc: PowerOfAttorney,
        organizations: List<PowerOfAttorney.Organization>,
        individuals: List<PowerOfAttorney.Individual>,
        suppliers: List<PowerOfAttorney.Supplier>,
        products: List<PowerOfAttorney.Product>
    ) {
        selectedDocumentId = doc.header.id
        number = doc.header.number.toString()
        dischargeDate = DATE_FORMAT.format(doc.header.dischargeDate)
        endDate = DATE_FORMAT.format(doc.header.endDate)
        selectedOrganization = organizations.firstOrNull { it.id == doc.header.organization.id } ?: doc.header.organization
        selectedIndividual = individuals.firstOrNull { it.id == doc.header.individual.id } ?: doc.header.individual
        selectedSupplier = suppliers.firstOrNull { it.id == doc.header.supplier.id } ?: doc.header.supplier
        supplierAgreement = doc.header.supplierAgreement
        bodyItems = if (doc.body.isNotEmpty()) {
            doc.body.map { body ->
                PowerOfAttorneyBodyItem(
                    product = products.firstOrNull { it.id == body.product.id } ?: body.product,
                    unit = body.unit,
                    count = body.count
                )
            }
        } else {
            listOf(PowerOfAttorneyBodyItem(product = products.firstOrNull()))
        }
    }

    fun addBodyRow(product: PowerOfAttorney.Product?) {
        bodyItems = bodyItems + PowerOfAttorneyBodyItem(product = product)
    }

    fun updateBodyRow(index: Int, row: PowerOfAttorneyBodyItem) {
        bodyItems = bodyItems.toMutableList().also {
            if (index in it.indices) it[index] = row
        }
    }

    fun removeBodyRow(index: Int, defaultProduct: PowerOfAttorney.Product?) {
        bodyItems = if (bodyItems.size > 1) {
            bodyItems.filterIndexed { i, _ -> i != index }
        } else {
            listOf(PowerOfAttorneyBodyItem(product = defaultProduct))
        }
    }

    fun parseDischargeDate(): LocalDate {
        return runCatching { DATE_FORMAT.parse(dischargeDate) }
            .getOrElse { runCatching { LocalDate.parse(dischargeDate) }.getOrElse { today } }
    }

    fun parseEndDate(): LocalDate {
        return runCatching { DATE_FORMAT.parse(endDate) }
            .getOrElse { runCatching { LocalDate.parse(endDate) }.getOrElse { defaultEndDate } }
    }

    fun toPowerOfAttorney(
        headerId: Int = selectedDocumentId ?: 0,
        defaultProduct: PowerOfAttorney.Product? = null
    ): PowerOfAttorney? {
        val org = selectedOrganization ?: return null
        val ind = selectedIndividual ?: return null
        val sup = selectedSupplier ?: return null

        val header = PowerOfAttorney.Header(
            id = headerId,
            organization = org,
            number = number.toIntOrNull() ?: 0,
            dischargeDate = parseDischargeDate(),
            endDate = parseEndDate(),
            individual = ind,
            supplier = sup,
            supplierAgreement = supplierAgreement
        )

        val bodyList = bodyItems.map { item ->
            PowerOfAttorney.Body(
                id = 0,
                count = item.count,
                unit = item.unit,
                header = header,
                product = item.product ?: defaultProduct ?: PowerOfAttorney.Product(name = "Товар")
            )
        }

        return PowerOfAttorney(header, bodyList)
    }

    fun prepareExportData(): Pair<Map<String, String>, Map<String, List<String>>> {
        val org = selectedOrganization
        val ind = selectedIndividual
        val sup = selectedSupplier

        val head = mapOf(
            "{{number}}" to number,
            "{{dischargeDate}}" to dischargeDate,
            "{{endDate}}" to endDate,
            "{{organizationName}}" to (org?.name ?: ""),
            "{{organizationConsumer}}" to (org?.consumer ?: ""),
            "{{organizationPayer}}" to (org?.payer ?: ""),
            "{{organizationAccount}}" to (org?.account ?: ""),
            "{{individualJob}}" to (ind?.job ?: ""),
            "{{individualName}}" to (ind?.name ?: ""),
            "{{individualSeries}}" to (ind?.series ?: ""),
            "{{individualNumber}}" to (ind?.number ?: ""),
            "{{individualIssued}}" to (ind?.issued ?: ""),
            "{{individualDate}}" to (ind?.date?.toString() ?: ""),
            "{{supplierName}}" to (sup?.name ?: ""),
            "{{supplierAgreement}}" to supplierAgreement
        )

        val body = mapOf(
            "Номер по порядку" to List(bodyItems.size) { idx -> (idx + 1).toString() },
            "Материальные ценности" to bodyItems.map { it.product?.name ?: "" },
            "Единица измерения" to bodyItems.map { it.unit },
            "Количество (прописью)" to bodyItems.map { it.count }
        )

        return head to body
    }
}