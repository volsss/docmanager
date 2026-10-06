package ru.example.docmanager.ui.screens.forms.powerOfAttorney

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.util.fastFilteredMap
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.LocalDate
import kotlinx.datetime.plus
import ru.example.docmanager.documents.PowerOfAttorney
import ru.example.docmanager.references.Individual
import ru.example.docmanager.references.Organization
import ru.example.docmanager.references.Product
import ru.example.docmanager.references.base.Reference
import ru.example.docmanager.references.base.ReferenceType
import ru.example.docmanager.references.Supplier
import ru.example.docmanager.ui.Utils
import ru.example.docmanager.ui.screens.forms.FormState

class PowerOfAttorneyFormState(
    today: LocalDate
): FormState<PowerOfAttorney, PowerOfAttorneyBodyItem>(today) {
    val defaultEndDate = today.plus(DatePeriod(days = 30))
    var endDate by mutableStateOf(Utils.DATE_FORMAT.format(defaultEndDate))
    var selectedOrganization by mutableStateOf<Organization?>(null)
    var selectedIndividual by mutableStateOf<Individual?>(null)
    var selectedSupplier by mutableStateOf<Supplier?>(null)
    var supplierAgreement by mutableStateOf("")

    override fun addBodyItem() {
        bodyItems = bodyItems + PowerOfAttorneyBodyItem(
            id = bodyItems.size
        )
    }

    override fun resetForm(references: Map<ReferenceType, List<Reference>>) {
        selectedDocumentId = null
        val nextNumber = if (existingDocuments.isNotEmpty()) {
            (existingDocuments.maxOfOrNull { it.header.number } ?: 0) + 1
        } else 1
        number = nextNumber.toString()
        dischargeDate = Utils.DATE_FORMAT.format(today)
        endDate = Utils.DATE_FORMAT.format(defaultEndDate)
        selectedOrganization = references[ReferenceType.ORGANIZATION]?.firstOrNull() as Organization?
        selectedIndividual = references[ReferenceType.INDIVIDUAL]?.firstOrNull() as Individual?
        selectedSupplier = references[ReferenceType.SUPPLIER]?.firstOrNull() as Supplier?
        supplierAgreement = ""
        bodyItems = listOf()
        statusMessage = null
    }

    override fun populateFromDocument(
        document: PowerOfAttorney,
        references: Map<ReferenceType, List<Reference>>
    ) {
        selectedDocumentId = document.header.id
        number = document.header.number.toString()
        dischargeDate = Utils.DATE_FORMAT.format(document.header.dischargeDate)
        endDate = Utils.DATE_FORMAT.format(document.header.endDate)
        selectedOrganization = references[ReferenceType.ORGANIZATION]?.firstOrNull {
            it.id == document.header.organization.id
        } as Organization? ?: document.header.organization
        selectedIndividual = references[ReferenceType.INDIVIDUAL]?.firstOrNull {
            it.id == document.header.individual.id
        } as Individual? ?: document.header.individual
        selectedSupplier = references[ReferenceType.SUPPLIER]?.firstOrNull {
            it.id == document.header.supplier.id
        } as Supplier? ?: document.header.supplier
        supplierAgreement = document.header.supplierAgreement
        bodyItems = if (document.body.items.isNotEmpty()) {
            document.body.items.map { body ->
                PowerOfAttorneyBodyItem(
                    id = body.id,
                    product = references[ReferenceType.PRODUCT]?.firstOrNull {
                        it.id == body.product.id
                    } as Product? ?: body.product,
                    unit = body.unit,
                    count = body.count
                )
            }
        } else {
            listOf()
        }
    }

    override fun toDocument(): PowerOfAttorney? {
        val org = selectedOrganization ?: return null
        val ind = selectedIndividual ?: return null
        val sup = selectedSupplier ?: return null

        val dis = runCatching { Utils.DATE_FORMAT.parse(dischargeDate) }
            .getOrElse { runCatching { LocalDate.parse(dischargeDate) }.getOrElse { today } }

        val end = runCatching { Utils.DATE_FORMAT.parse(endDate) }
            .getOrElse { runCatching { LocalDate.parse(endDate) }.getOrElse { defaultEndDate } }

        val header = PowerOfAttorney.Header(
            id = selectedDocumentId ?: return null,
            organization = org,
            number = number.toIntOrNull() ?: 0,
            dischargeDate = dis,
            endDate = end,
            individual = ind,
            supplier = sup,
            supplierAgreement = supplierAgreement
        )

        val bodyItemList = bodyItems.fastFilteredMap(
            { it.product != null },
        ) { item ->
            PowerOfAttorney.BodyItem(
                id = 0,
                count = item.count,
                unit = item.unit,
                header = header,
                product = item.product!!
            )
        }

        val body = PowerOfAttorney.Body(bodyItemList)

        return PowerOfAttorney(header, body)
    }
}