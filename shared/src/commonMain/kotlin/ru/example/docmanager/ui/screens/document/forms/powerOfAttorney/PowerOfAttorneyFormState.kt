/*
 * Copyright (c) 2026 Andrew Z.
 * docmanager — a scalable document management system.
 */

package ru.example.docmanager.ui.screens.document.forms.powerOfAttorney

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.util.fastFilteredMap
import kotlinx.datetime.*
import ru.example.docmanager.data.document.type.powerofattorney.PowerOfAttorney
import ru.example.docmanager.data.document.type.powerofattorney.PowerOfAttorneyBody
import ru.example.docmanager.data.document.type.powerofattorney.PowerOfAttorneyBodyItem
import ru.example.docmanager.data.document.type.powerofattorney.PowerOfAttorneyHeader
import ru.example.docmanager.data.reference.Individual
import ru.example.docmanager.data.reference.Organization
import ru.example.docmanager.data.reference.Product
import ru.example.docmanager.data.reference.Supplier
import ru.example.docmanager.ui.DATE_FORMAT
import ru.example.docmanager.ui.screens.document.forms.FormState
import ru.example.docmanager.ui.toRussianString
import kotlin.time.Clock

data class PowerOfAttorneyFormState(
    val individuals: List<Individual> = emptyList(),
    val organizations: List<Organization> = emptyList(),
    val products: List<Product> = emptyList(),
    val suppliers: List<Supplier> = emptyList(),
) : FormState<PowerOfAttorney, PowerOfAttorneyBodyItemState>(
    Clock.System.todayIn(TimeZone.currentSystemDefault())
) {
    val defaultEndDate = today.plus(DatePeriod(days = 30))
    var endDate by mutableStateOf(defaultEndDate.toRussianString())
    var selectedOrganization by mutableStateOf<Organization?>(null)
    var selectedIndividual by mutableStateOf<Individual?>(null)
    var selectedSupplier by mutableStateOf<Supplier?>(null)
    var supplierAgreement by mutableStateOf("")

    override fun addBodyItem() {
        bodyItems = bodyItems + PowerOfAttorneyBodyItemState(
            id = bodyItems.size
        )
    }

    override fun resetForm() {
        selectedDocumentId = null
        val nextNumber = if (existingDocuments.isNotEmpty()) {
            (existingDocuments.maxOfOrNull { it.header.number } ?: 0) + 1
        } else 1
        number = nextNumber.toString()
        dischargeDate = today.toRussianString()
        endDate = defaultEndDate.toRussianString()
        selectedOrganization = organizations.firstOrNull()
        selectedIndividual = individuals.firstOrNull()
        selectedSupplier = suppliers.firstOrNull()
        supplierAgreement = ""
        bodyItems = listOf()
        statusMessage = null
    }

    override fun populateFromDocument(
        document: PowerOfAttorney
    ) {
        selectedDocumentId = document.header.id
        number = document.header.number.toString()
        dischargeDate = document.header.dischargeDate.toRussianString()
        endDate = document.header.endDate.toRussianString()
        selectedOrganization =
            organizations.firstOrNull {
                it.id == document.header.organization.id
            } ?: document.header.organization
        selectedIndividual =
            individuals.firstOrNull {
                it.id == document.header.individual.id
            } ?: document.header.individual
        selectedSupplier =
            suppliers.firstOrNull {
                it.id == document.header.supplier.id
            } ?: document.header.supplier
        supplierAgreement = document.header.supplierAgreement
        bodyItems = if (document.body.items.isNotEmpty()) {
            document.body.items.map { body ->
                PowerOfAttorneyBodyItemState(
                    id = body.id,
                    product = products.firstOrNull {
                        it.id == body.product.id
                    } ?: body.product,
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

        val dis = runCatching {
            DATE_FORMAT.parse(dischargeDate)
        }.getOrElse {
            runCatching {
                LocalDate.parse(dischargeDate)
            }.getOrElse { today }
        }

        val end = runCatching {
            DATE_FORMAT.parse(endDate)
        }.getOrElse {
            runCatching {
                LocalDate.parse(endDate)
            }.getOrElse { defaultEndDate }
        }

        val header = PowerOfAttorneyHeader(
            id = selectedDocumentId ?: 0,
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
            PowerOfAttorneyBodyItem(
                id = 0,
                count = item.count,
                unit = item.unit,
                product = item.product!!
            )
        }

        val body = PowerOfAttorneyBody(bodyItemList)

        return PowerOfAttorney(header, body)
    }
}